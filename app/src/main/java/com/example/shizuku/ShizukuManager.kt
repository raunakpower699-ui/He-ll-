package com.example.shizuku

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

data class ShellResult(
    val exitCode: Int,
    val output: String,
    val error: String,
    val isSuccess: Boolean
)

class ShizukuManager(private val context: Context) {
    companion object {
        const val TAG = "ShizukuManager"
        const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
        const val PERMISSION_REQUEST_CODE = 4421
    }

    private val _status = MutableStateFlow<ShizukuStatus>(ShizukuStatus.NotInstalled)
    val status: StateFlow<ShizukuStatus> = _status.asStateFlow()

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        checkStatus()
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        _status.value = ShizukuStatus.ServiceStopped
    }

    private val requestPermissionResultListener =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == PERMISSION_REQUEST_CODE) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    checkStatus()
                } else {
                    _status.value = ShizukuStatus.PermissionRequired
                }
            }
        }

    init {
        try {
            Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
            Shizuku.addBinderDeadListener(binderDeadListener)
            Shizuku.addRequestPermissionResultListener(requestPermissionResultListener)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to register Shizuku listeners", e)
        }
        checkStatus()
    }

    fun cleanUp() {
        try {
            Shizuku.removeBinderReceivedListener(binderReceivedListener)
            Shizuku.removeBinderDeadListener(binderDeadListener)
            Shizuku.removeRequestPermissionResultListener(requestPermissionResultListener)
        } catch (e: Throwable) {
            Log.e(TAG, "Error removing Shizuku listeners", e)
        }
    }

    fun isShizukuInstalled(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    SHIZUKU_PACKAGE,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(SHIZUKU_PACKAGE, 0)
            }
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        } catch (_: Throwable) {
            false
        }
    }

    fun checkStatus(): ShizukuStatus {
        val newStatus = try {
            if (!isShizukuInstalled()) {
                ShizukuStatus.NotInstalled
            } else if (!Shizuku.pingBinder()) {
                ShizukuStatus.ServiceStopped
            } else {
                val hasPermission = if (Shizuku.isPreV11()) {
                    false
                } else {
                    Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
                }

                if (hasPermission) {
                    val version = try { Shizuku.getVersion() } catch (_: Throwable) { 13 }
                    val uid = try { Shizuku.getUid() } catch (_: Throwable) { 2000 }
                    ShizukuStatus.Authorized(version, uid)
                } else {
                    ShizukuStatus.PermissionRequired
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Exception during checkStatus", e)
            if (isShizukuInstalled()) ShizukuStatus.ServiceStopped else ShizukuStatus.NotInstalled
        }

        _status.value = newStatus
        return newStatus
    }

    fun requestPermission() {
        try {
            if (Shizuku.pingBinder()) {
                if (Shizuku.shouldShowRequestPermissionRationale()) {
                    // Direct request or rationale
                    Shizuku.requestPermission(PERMISSION_REQUEST_CODE)
                } else {
                    Shizuku.requestPermission(PERMISSION_REQUEST_CODE)
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error requesting Shizuku permission", e)
        }
    }

    suspend fun executeCommand(command: String): ShellResult = withContext(Dispatchers.IO) {
        if (!Shizuku.pingBinder()) {
            return@withContext ShellResult(
                exitCode = -1,
                output = "",
                error = "Shizuku service is not running or binder is dead",
                isSuccess = false
            )
        }

        try {
            val method = Shizuku::class.java.getDeclaredMethod(
                "newProcess",
                Array<String>::class.java,
                Array<String>::class.java,
                String::class.java
            )
            method.isAccessible = true
            val process = method.invoke(null, arrayOf("sh", "-c", command), null, null) as Process
            val stdout = StringBuilder()
            val stderr = StringBuilder()

            val stdoutReader = BufferedReader(InputStreamReader(process.inputStream))
            val stderrReader = BufferedReader(InputStreamReader(process.errorStream))

            var line: String?
            while (stdoutReader.readLine().also { line = it } != null) {
                stdout.append(line).append("\n")
            }
            while (stderrReader.readLine().also { line = it } != null) {
                stderr.append(line).append("\n")
            }

            val exitCode = process.waitFor()
            ShellResult(
                exitCode = exitCode,
                output = stdout.toString().trim(),
                error = stderr.toString().trim(),
                isSuccess = exitCode == 0
            )
        } catch (e: Throwable) {
            Log.e(TAG, "Failed executing Shizuku command: $command", e)
            ShellResult(
                exitCode = -1,
                output = "",
                error = e.localizedMessage ?: "Unknown shell execution error",
                isSuccess = false
            )
        }
    }
}
