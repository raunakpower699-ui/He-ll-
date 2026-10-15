package com.example.fps

enum class EvaluationStatus(val title: String) {
    SUPPORTED_AND_APPLIED("Supported & Configured"),
    ALREADY_ENABLED("Already Active"),
    UNSUPPORTED_BY_HARDWARE("Hardware Incompatible"),
    UNSUPPORTED_BY_GAME_ENGINE("Engine Limit Exceeded"),
    PARTNER_WHITELIST_REQUIRED("OEM Whitelist Required"),
    GAME_NOT_INSTALLED("Game Not Installed"),
    PERMISSION_REQUIRED("Shizuku Permission Required"),
    SHIZUKU_DISCONNECTED("Shizuku Disconnected"),
    CHANGE_NOT_VERIFIED("Change Unverified")
}

data class EvaluationResult(
    val status: EvaluationStatus,
    val targetFps: FpsTarget,
    val game: GameProfile,
    val currentDisplayHz: Float,
    val maxDisplayHz: Float,
    val isGameInstalled: Boolean,
    val gameVersionName: String?,
    val gameVersionCode: Long?,
    val systemPeakRefreshRate: String?,
    val systemUserRefreshRate: String?,
    val vivoMonsterModeEnabled: Boolean?,
    val isVivoDevice: Boolean,
    val explanation: String,
    val hindiExplanation: String,
    val inGameSettingInstructions: List<String>,
    val technicalDetails: List<String>,
    val recommendation: String,
    val antiCheatSafetyConfirmed: Boolean = true
)
