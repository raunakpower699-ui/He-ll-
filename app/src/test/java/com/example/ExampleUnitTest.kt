package com.example

import com.example.fps.EvaluationStatus
import com.example.fps.FpsTarget
import com.example.fps.GameProfile
import com.example.shizuku.ShizukuStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testGameProfilesEngineLimits() {
        assertEquals("com.pubg.imobile", GameProfile.BGMI.packageId)
        assertEquals(120, GameProfile.BGMI.maxEngineFps)
        assertTrue(GameProfile.BGMI.nativeSupportedTiers.contains(120))
        assertFalse(GameProfile.BGMI.nativeSupportedTiers.contains(144))
        assertFalse(GameProfile.BGMI.nativeSupportedTiers.contains(165))

        assertEquals("com.dts.freefireth", GameProfile.FREE_FIRE.packageId)
        assertEquals(120, GameProfile.FREE_FIRE.maxEngineFps)

        assertEquals("com.dts.freefiremax", GameProfile.FREE_FIRE_MAX.packageId)
        assertEquals(120, GameProfile.FREE_FIRE_MAX.maxEngineFps)
    }

    @Test
    fun testFpsTargets() {
        assertEquals(60, FpsTarget.FPS_60.fps)
        assertEquals(90, FpsTarget.FPS_90.fps)
        assertEquals(120, FpsTarget.FPS_120.fps)
        assertEquals(144, FpsTarget.FPS_144.fps)
        assertEquals(165, FpsTarget.FPS_165.fps)

        assertEquals(FpsTarget.FPS_144, FpsTarget.fromFps(144))
        assertEquals(FpsTarget.FPS_165, FpsTarget.fromFps(165))
    }

    @Test
    fun testShizukuStatusTransitions() {
        val notInstalled = ShizukuStatus.NotInstalled
        assertFalse(notInstalled.isReady)
        assertEquals("Shizuku Not Installed", notInstalled.displayLabel)

        val serviceStopped = ShizukuStatus.ServiceStopped
        assertFalse(serviceStopped.isReady)
        assertEquals("Shizuku Service Stopped", serviceStopped.displayLabel)

        val permRequired = ShizukuStatus.PermissionRequired
        assertFalse(permRequired.isReady)
        assertEquals("Authorization Required", permRequired.displayLabel)

        val authorized = ShizukuStatus.Authorized(version = 13, uid = 2000)
        assertTrue(authorized.isReady)
        assertEquals("Connected & Authorized (v13)", authorized.displayLabel)
    }

    @Test
    fun testEvaluationStatusCategories() {
        assertNotNull(EvaluationStatus.SUPPORTED_AND_APPLIED)
        assertNotNull(EvaluationStatus.UNSUPPORTED_BY_HARDWARE)
        assertNotNull(EvaluationStatus.UNSUPPORTED_BY_GAME_ENGINE)
        assertNotNull(EvaluationStatus.PARTNER_WHITELIST_REQUIRED)
    }
}
