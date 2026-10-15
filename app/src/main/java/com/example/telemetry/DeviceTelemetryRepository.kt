package com.example.telemetry

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.display.DisplayManager
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.view.Display
import android.view.WindowManager
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

data class DisplayModeInfo(
    val modeId: Int,
    val width: Int,
    val height: Int,
    val refreshRate: Float
)

data class DeviceTelemetry(
    val manufacturer: String,
    val brand: String,
    val model: String,
    val androidVersion: String,
    val apiLevel: Int,
    val isVivoOrIqoo: Boolean,
    val vivoOsVersion: String?,
    val currentRefreshRate: Float,
    val maxSupportedRefreshRate: Float,
    val supportedModes: List<DisplayModeInfo>,
    val totalRamMb: Long,
    val availableRamMb: Long,
    val isLowRam: Boolean,
    val batteryLevel: Int,
    val batteryTemperatureC: Float,
    val batteryStatus: String,
    val thermalStatus: String,
    val thermalHeadroom: Float?,
    val cpuArchitecture: String,
    val cpuCores: Int,
    val cpuHardwareName: String?
)

class DeviceTelemetryRepository(private val context: Context) {

    fun getTelemetry(): DeviceTelemetry {
        val manufacturer = Build.MANUFACTURER.orEmpty()
        val brand = Build.BRAND.orEmpty()
        val model = Build.MODEL.orEmpty()
        val isVivoOrIqoo = manufacturer.contains("vivo", ignoreCase = true) ||
                brand.contains("vivo", ignoreCase = true) ||
                manufacturer.contains("iqoo", ignoreCase = true) ||
                brand.contains("iqoo", ignoreCase = true)

        val vivoOsVersion = detectVivoOsVersion()

        // Display & Refresh Rate
        val (currentHz, maxHz, modes) = readDisplayInfo()

        // Memory
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager?.getMemoryInfo(memInfo)
        val totalRamMb = memInfo.totalMem / (1024 * 1024)
        val availRamMb = memInfo.availMem / (1024 * 1024)

        // Battery
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val rawLevel = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (rawLevel >= 0 && scale > 0) (rawLevel * 100) / scale else -1
        val tempRaw = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val tempC = if (tempRaw > 0) tempRaw / 10.0f else 0.0f

        val statusInt = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val batteryStatus = when (statusInt) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
            else -> "Unknown"
        }

        // Thermal
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val thermalStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && powerManager != null) {
            when (powerManager.currentThermalStatus) {
                PowerManager.THERMAL_STATUS_NONE -> "Nominal (Cool)"
                PowerManager.THERMAL_STATUS_LIGHT -> "Light Throttle"
                PowerManager.THERMAL_STATUS_MODERATE -> "Moderate Throttle"
                PowerManager.THERMAL_STATUS_SEVERE -> "Severe Throttle"
                PowerManager.THERMAL_STATUS_CRITICAL -> "Critical Heat"
                PowerManager.THERMAL_STATUS_EMERGENCY -> "Emergency Heat"
                PowerManager.THERMAL_STATUS_SHUTDOWN -> "Thermal Shutdown"
                else -> "Normal"
            }
        } else {
            "N/A"
        }

        val thermalHeadroom = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && powerManager != null) {
            try {
                powerManager.getThermalHeadroom(30)
            } catch (_: Throwable) {
                null
            }
        } else {
            null
        }

        // CPU & Hardware
        val cpuArch = Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown ABI"
        val cpuCores = Runtime.getRuntime().availableProcessors()
        val cpuHardware = readCpuHardware() ?: Build.HARDWARE

        return DeviceTelemetry(
            manufacturer = manufacturer,
            brand = brand,
            model = model,
            androidVersion = "Android ${Build.VERSION.RELEASE}",
            apiLevel = Build.VERSION.SDK_INT,
            isVivoOrIqoo = isVivoOrIqoo,
            vivoOsVersion = vivoOsVersion,
            currentRefreshRate = currentHz,
            maxSupportedRefreshRate = maxHz,
            supportedModes = modes,
            totalRamMb = totalRamMb,
            availableRamMb = availRamMb,
            isLowRam = memInfo.lowMemory,
            batteryLevel = batteryPct,
            batteryTemperatureC = tempC,
            batteryStatus = batteryStatus,
            thermalStatus = thermalStatus,
            thermalHeadroom = thermalHeadroom,
            cpuArchitecture = cpuArch,
            cpuCores = cpuCores,
            cpuHardwareName = cpuHardware
        )
    }

    private fun readDisplayInfo(): Triple<Float, Float, List<DisplayModeInfo>> {
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
            displayManager?.getDisplay(Display.DEFAULT_DISPLAY)
        } else {
            @Suppress("DEPRECATION")
            (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay
        }

        if (display == null) {
            return Triple(60.0f, 60.0f, emptyList())
        }

        val currentHz = display.refreshRate
        val modes = display.supportedModes.map { mode ->
            DisplayModeInfo(
                modeId = mode.modeId,
                width = mode.physicalWidth,
                height = mode.physicalHeight,
                refreshRate = mode.refreshRate
            )
        }

        val maxHz = modes.maxOfOrNull { it.refreshRate } ?: currentHz
        return Triple(currentHz, maxHz, modes)
    }

    private fun detectVivoOsVersion(): String? {
        // Try reading known Vivo/OriginOS system properties via reflection or System.getProperty
        val keys = listOf(
            "ro.vivo.os.build.display.id",
            "ro.vivo.os.version",
            "ro.vivo.product.model",
            "ro.iqoo.os.version"
        )
        for (key in keys) {
            val value = getSystemProperty(key)
            if (!value.isNullOrBlank()) {
                return value
            }
        }
        return null
    }

    private fun getSystemProperty(key: String): String? {
        return try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val getMethod = systemPropertiesClass.getMethod("get", String::class.java)
            val result = getMethod.invoke(null, key) as? String
            if (result.isNullOrBlank()) null else result
        } catch (_: Throwable) {
            null
        }
    }

    private fun readCpuHardware(): String? {
        return try {
            val cpuInfo = File("/proc/cpuinfo")
            if (cpuInfo.exists() && cpuInfo.canRead()) {
                BufferedReader(FileReader(cpuInfo)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val current = line.orEmpty()
                        if (current.startsWith("Hardware", ignoreCase = true) ||
                            current.startsWith("Processor", ignoreCase = true) ||
                            current.startsWith("model name", ignoreCase = true)
                        ) {
                            val parts = current.split(":")
                            if (parts.size >= 2) {
                                return parts[1].trim()
                            }
                        }
                    }
                }
            }
            null
        } catch (_: Throwable) {
            null
        }
    }
}
