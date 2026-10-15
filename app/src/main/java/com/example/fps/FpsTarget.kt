package com.example.fps

enum class FpsTarget(val fps: Int, val label: String, val badge: String) {
    FPS_60(60, "60 FPS", "Standard"),
    FPS_90(90, "90 FPS", "Smooth"),
    FPS_120(120, "120 FPS", "Pro Esports"),
    FPS_144(144, "144 FPS", "Ultra High"),
    FPS_165(165, "165 FPS", "Extreme");

    companion object {
        fun fromFps(fps: Int): FpsTarget = entries.find { it.fps == fps } ?: FPS_120
        fun fromName(name: String): FpsTarget = entries.find { it.name == name } ?: FPS_120
    }
}
