package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.fps.FpsTarget
import com.example.fps.GameProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("raunak_exploits_prefs", Context.MODE_PRIVATE)

    private val _selectedGame = MutableStateFlow(loadSelectedGame())
    val selectedGame: StateFlow<GameProfile> = _selectedGame.asStateFlow()

    private val _targetFps = MutableStateFlow(loadTargetFps())
    val targetFps: StateFlow<FpsTarget> = _targetFps.asStateFlow()

    private val _lastSummary = MutableStateFlow(prefs.getString(KEY_LAST_SUMMARY, "No evaluation performed yet") ?: "")
    val lastSummary: StateFlow<String> = _lastSummary.asStateFlow()

    private val _lastTimestamp = MutableStateFlow(prefs.getLong(KEY_LAST_TIMESTAMP, 0L))
    val lastTimestamp: StateFlow<Long> = _lastTimestamp.asStateFlow()

    private val _lastStatusType = MutableStateFlow(prefs.getString(KEY_LAST_STATUS_TYPE, "READY") ?: "READY")
    val lastStatusType: StateFlow<String> = _lastStatusType.asStateFlow()

    private val _allow144And165Fps = MutableStateFlow(prefs.getBoolean(KEY_ALLOW_144_165, false))
    val allow144And165Fps: StateFlow<Boolean> = _allow144And165Fps.asStateFlow()

    private fun loadSelectedGame(): GameProfile {
        val name = prefs.getString(KEY_SELECTED_GAME, GameProfile.BGMI.name) ?: GameProfile.BGMI.name
        return GameProfile.fromName(name)
    }

    private fun loadTargetFps(): FpsTarget {
        val name = prefs.getString(KEY_TARGET_FPS, FpsTarget.FPS_120.name) ?: FpsTarget.FPS_120.name
        return FpsTarget.fromName(name)
    }

    fun setSelectedGame(game: GameProfile) {
        prefs.edit().putString(KEY_SELECTED_GAME, game.name).apply()
        _selectedGame.value = game
    }

    fun setTargetFps(target: FpsTarget) {
        prefs.edit().putString(KEY_TARGET_FPS, target.name).apply()
        _targetFps.value = target
    }

    fun saveEvaluationResult(summary: String, statusType: String) {
        val time = System.currentTimeMillis()
        prefs.edit()
            .putString(KEY_LAST_SUMMARY, summary)
            .putString(KEY_LAST_STATUS_TYPE, statusType)
            .putLong(KEY_LAST_TIMESTAMP, time)
            .apply()
        _lastSummary.value = summary
        _lastStatusType.value = statusType
        _lastTimestamp.value = time
    }

    fun setAllow144And165Fps(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ALLOW_144_165, enabled).apply()
        _allow144And165Fps.value = enabled
        if (!enabled && (_targetFps.value == FpsTarget.FPS_144 || _targetFps.value == FpsTarget.FPS_165)) {
            setTargetFps(FpsTarget.FPS_120)
        }
    }

    companion object {
        private const val KEY_SELECTED_GAME = "key_selected_game"
        private const val KEY_TARGET_FPS = "key_target_fps"
        private const val KEY_LAST_SUMMARY = "key_last_summary"
        private const val KEY_LAST_STATUS_TYPE = "key_last_status_type"
        private const val KEY_LAST_TIMESTAMP = "key_last_timestamp"
        private const val KEY_ALLOW_144_165 = "key_allow_144_165"
    }
}
