package com.example.fps

enum class GameProfile(
    val packageId: String,
    val displayName: String,
    val engineName: String,
    val maxEngineFps: Int,
    val nativeSupportedTiers: List<Int>,
    val description: String
) {
    BGMI(
        packageId = "com.pubg.imobile",
        displayName = "BGMI",
        engineName = "Unreal Engine 4 Mobile",
        maxEngineFps = 120,
        nativeSupportedTiers = listOf(30, 40, 60, 90, 120),
        description = "Battlegrounds Mobile India. Supports 60 FPS (Extreme), 90 FPS on whitelisted 90Hz/120Hz devices, and 120 FPS on select flagship SoCs (Snapdragon 8 Gen 2/3, Dimensity 9200+)."
    ),
    FREE_FIRE(
        packageId = "com.dts.freefireth",
        displayName = "Free Fire",
        engineName = "Unity Game Engine",
        maxEngineFps = 120,
        nativeSupportedTiers = listOf(30, 60, 90, 120),
        description = "Garena Free Fire. Standard High FPS toggle enables 60 FPS. Select partner OEM flagships have 90/120 FPS unlocked by client server configuration."
    ),
    FREE_FIRE_MAX(
        packageId = "com.dts.freefiremax",
        displayName = "Free Fire MAX",
        engineName = "Unity HD Engine",
        maxEngineFps = 120,
        nativeSupportedTiers = listOf(30, 60, 90, 120),
        description = "Garena Free Fire MAX with higher graphical fidelity. Supports High FPS (60 FPS) natively; 120 FPS restricted to whitelisted partner devices."
    );

    companion object {
        fun fromPackageId(id: String): GameProfile? = entries.find { it.packageId == id }
        fun fromName(name: String): GameProfile = entries.find { it.name == name } ?: BGMI
    }
}
