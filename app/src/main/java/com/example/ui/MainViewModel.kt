package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PreferencesManager
import com.example.fps.EvaluationResult
import com.example.fps.FpsCompatibilityEvaluator
import com.example.fps.FpsTarget
import com.example.fps.GameProfile
import com.example.shizuku.ShizukuManager
import com.example.shizuku.ShizukuStatus
import com.example.telemetry.DeviceTelemetry
import com.example.telemetry.DeviceTelemetryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)
    val shizukuManager = ShizukuManager(application)
    val telemetryRepo = DeviceTelemetryRepository(application)
    private val evaluator = FpsCompatibilityEvaluator(application, shizukuManager, telemetryRepo)

    val shizukuStatus: StateFlow<ShizukuStatus> = shizukuManager.status
    val selectedGame: StateFlow<GameProfile> = prefs.selectedGame
    val targetFps: StateFlow<FpsTarget> = prefs.targetFps
    val allow144And165Fps: StateFlow<Boolean> = prefs.allow144And165Fps

    private val _telemetry = MutableStateFlow(telemetryRepo.getTelemetry())
    val telemetry: StateFlow<DeviceTelemetry> = _telemetry.asStateFlow()

    private val _isEvaluating = MutableStateFlow(false)
    val isEvaluating: StateFlow<Boolean> = _isEvaluating.asStateFlow()

    private val _evaluationResult = MutableStateFlow<EvaluationResult?>(null)
    val evaluationResult: StateFlow<EvaluationResult?> = _evaluationResult.asStateFlow()

    private val _diagnosticLogs = MutableStateFlow<List<String>>(emptyList())
    val diagnosticLogs: StateFlow<List<String>> = _diagnosticLogs.asStateFlow()

    private val _shellOutput = MutableStateFlow("")
    val shellOutput: StateFlow<String> = _shellOutput.asStateFlow()

    private var evaluationJob: Job? = null

    init {
        refreshTelemetry()
        appendLog("RAUNAK EXPLOITS initialized.")
        appendLog("Detected: ${_telemetry.value.manufacturer} ${_telemetry.value.model} (${_telemetry.value.androidVersion})")
        appendLog("Panel Refresh Rate: ${_telemetry.value.currentRefreshRate.toInt()} Hz (Max: ${_telemetry.value.maxSupportedRefreshRate.toInt()} Hz)")
        appendLog("Shizuku Status: ${shizukuStatus.value.displayLabel}")
    }

    override fun onCleared() {
        super.onCleared()
        shizukuManager.cleanUp()
    }

    fun selectGame(game: GameProfile) {
        prefs.setSelectedGame(game)
        appendLog("Selected Game Profile: ${game.displayName}")
    }

    fun selectFpsTarget(target: FpsTarget) {
        prefs.setTargetFps(target)
        appendLog("Target FPS configured to: ${target.label}")
    }

    fun toggleAllow144And165Fps(enabled: Boolean) {
        prefs.setAllow144And165Fps(enabled)
        appendLog("144/165 FPS mode: ${if (enabled) "Enabled" else "Disabled (Free Fire/BGMI Cap at 120 FPS)"}")
    }

    fun launchGame(context: android.content.Context, game: GameProfile): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(game.packageId)
            if (intent != null) {
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                appendLog("Launched ${game.displayName} with active scanout lock.")
                true
            } else {
                appendLog("Cannot launch ${game.displayName}: Package not installed.")
                false
            }
        } catch (e: Throwable) {
            appendLog("Error launching ${game.displayName}: ${e.localizedMessage}")
            false
        }
    }

    fun refreshTelemetry() {
        _telemetry.value = telemetryRepo.getTelemetry()
        shizukuManager.checkStatus()
    }

    fun requestShizukuPermission() {
        appendLog("Requesting Shizuku authorization...")
        shizukuManager.requestPermission()
    }

    fun startEvaluation() {
        if (_isEvaluating.value) return

        evaluationJob?.cancel()
        evaluationJob = viewModelScope.launch {
            _isEvaluating.value = true
            appendLog("--- Starting Evaluation for ${selectedGame.value.displayName} @ ${targetFps.value.label} ---")
            
            // Brief animation delay for feedback
            delay(400)

            val result = evaluator.evaluate(selectedGame.value, targetFps.value)
            _evaluationResult.value = result

            result.technicalDetails.forEach { appendLog(it) }
            appendLog("Result Status: ${result.status.title}")

            prefs.saveEvaluationResult(
                summary = "${result.status.title}: ${result.explanation.take(90)}...",
                statusType = result.status.name
            )

            refreshTelemetry()
            _isEvaluating.value = false
        }
    }

    fun stopEvaluation() {
        evaluationJob?.cancel()
        _isEvaluating.value = false
        appendLog("Evaluation stopped/reset by user.")

        // Safely reset system peak refresh rate lock if Shizuku is authorized
        viewModelScope.launch {
            if (shizukuStatus.value is ShizukuStatus.Authorized) {
                appendLog("Resetting system peak_refresh_rate to auto...")
                shizukuManager.executeCommand("settings delete system peak_refresh_rate")
                shizukuManager.executeCommand("settings delete system min_refresh_rate")
                refreshTelemetry()
                appendLog("Display refresh rate unlocked to system default.")
            }
        }
    }

    fun runShellTest(command: String) {
        viewModelScope.launch {
            appendLog("Running diagnostics command: $command")
            val result = shizukuManager.executeCommand(command)
            val output = if (result.isSuccess) {
                result.output.ifBlank { "(Command succeeded with empty output)" }
            } else {
                "Error (code ${result.exitCode}): ${result.error.ifBlank { result.output }}"
            }
            _shellOutput.value = output
            appendLog("Shell output: $output")
        }
    }

    fun clearLogs() {
        _diagnosticLogs.value = emptyList()
    }

    private fun appendLog(message: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        _diagnosticLogs.value = _diagnosticLogs.value + "[$timestamp] $message"
    }
}
