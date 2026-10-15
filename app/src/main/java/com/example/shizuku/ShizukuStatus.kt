package com.example.shizuku

sealed interface ShizukuStatus {
    object NotInstalled : ShizukuStatus
    object ServiceStopped : ShizukuStatus
    object PermissionRequired : ShizukuStatus
    data class Authorized(val version: Int, val uid: Int) : ShizukuStatus

    val isReady: Boolean
        get() = this is Authorized

    val displayLabel: String
        get() = when (this) {
            is NotInstalled -> "Shizuku Not Installed"
            is ServiceStopped -> "Shizuku Service Stopped"
            is PermissionRequired -> "Authorization Required"
            is Authorized -> "Connected & Authorized (v$version)"
        }
}
