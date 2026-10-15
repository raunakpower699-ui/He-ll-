# RAUNAK EXPLOITS

**Professional Gaming Configuration & FPS Capability Investigator**  
*Optimized for Vivo & iQOO Smartphones • Rootless Shizuku Architecture*

---

## Overview

**RAUNAK EXPLOITS** is a specialized Android utility engineered for mobile gamers playing **BGMI (Battlegrounds Mobile India)**, **Free Fire**, and **Free Fire MAX** on modern Android devices, with dedicated tuning for **Vivo** (FuntouchOS) and **iQOO** (OriginOS) devices.

The application investigates whether high frame-rate modes (60, 90, 120, 144, and 165 FPS) are supported by both the device's physical hardware display scanout pipeline and the game's native client engine.

---

## Core Principles & Technical Honesty

1. **Non-Root Elevated System Access:** Uses the official **Shizuku API** (UID 2000 shell) to configure system display refresh rates without root or voiding warranty.
2. **Never Fake FPS:** The application never claims an FPS mode has been unlocked unless verified.
3. **Display Refresh Rate (Hz) vs Rendered FPS:** A 144 Hz display scanout rate does not guarantee 144 FPS in-game. Game frame rates are constrained by the game's client loop, server-side whitelists, and SoC render capabilities.
4. **Anti-Cheat & Fair Play Compliance:**
   - **Zero file tampering:** Does not alter game OBBs, APKs, or private game files.
   - **Zero memory injection:** Does not hook or inject code into game processes (ensuring compliance with Tencent ACE, BattlEye, and Garena anti-cheat).
   - **Zero device ID spoofing:** Explains developer OEM whitelists honestly.

---

## Supported Game Profiles

| Game Profile | Engine | Max Engine Limit | Native Setting Tiers |
| :--- | :--- | :--- | :--- |
| **BGMI** (`com.pubg.imobile`) | Unreal Engine 4 Mobile | **120 FPS** | 30, 40, 60 (Extreme), 90, 120 FPS |
| **Free Fire** (`com.dts.freefireth`) | Unity Engine | **120 FPS** | 30, 60 (High FPS), 90, 120 FPS |
| **Free Fire MAX** (`com.dts.freefiremax`) | Unity HD Engine | **120 FPS** | 30, 60 (High FPS), 90, 120 FPS |

> **Note on 144 FPS & 165 FPS:**  
> Neither BGMI nor Free Fire mobile engines contain 144 or 165 FPS render loops. RAUNAK EXPLOITS will report `Engine Limit Exceeded` when evaluating these targets.

---

## Vivo & iQOO Device Specifics

On **Vivo** and **iQOO** smartphones:
- **OS Compositor Throttling:** FuntouchOS / OriginOS often downclocks the display scanout to 60 Hz when 3D games run to conserve battery.
- **Peak Refresh Lock:** RAUNAK EXPLOITS pins `settings put system peak_refresh_rate` via Shizuku shell to ensure the physical panel maintains 90Hz/120Hz/144Hz while gaming.
- **Monster Mode / Ultra Game Mode:** Recommended on iQOO devices to prevent CPU/GPU thermal governor down-throttling.

---

## Shizuku Setup Guide (Non-Root)

1. Install the official **Shizuku** app (Google Play or GitHub).
2. Go to **Settings > About Phone** and tap **Build Number** 7 times to enable Developer Options.
3. In **Developer Options**, enable **USB Debugging** and **Wireless Debugging** (Android 11+).
4. Open Shizuku, select **Pairing**, enter the 6-digit Wi-Fi pairing code, and tap **Start**.
5. Launch **RAUNAK EXPLOITS** and tap **Authorize** when prompted.

## Free Fire & BGMI In-Game Graphics Activation

When **RAUNAK EXPLOITS** applies a 90 FPS or 120 FPS lock via Shizuku:
1. Tap **"Launch Game"** directly from the evaluation result card.
2. **For Free Fire / Free Fire MAX**:
   - Tap **Settings (Gear icon)** in the top right.
   - Go to **Display** tab.
   - Set **Graphics** to **Ultra** or **MAX** (Free Fire disables High FPS on Smooth/Standard).
   - Under **High FPS**, tap **High** (now unlocked to true 90/120 FPS because Shizuku removed Vivo's 60Hz limit).
3. **For BGMI**:
   - Go to **Settings > Graphics & Audio**.
   - Select **Smooth** graphics.
   - Select **90 fps** or **120 fps** in the Frame Rate bar.
4. **Verification**: Turn on **Show refresh rate** in Android **Developer Options** to see the live 90Hz/120Hz counter on your screen.

---

## Build & Run

```bash
# Compile and build debug APK
gradle assembleDebug

# Run unit tests
gradle :app:testDebugUnitTest
```

---

## Architecture

- **Language:** Kotlin 2.2.10
- **UI Toolkit:** Jetpack Compose + Material Design 3 (Dark Esports Aesthetic)
- **Shizuku:** `dev.rikka.shizuku:api:13.1.5` & `provider:13.1.5`
- **Architecture Pattern:** MVVM (`MainViewModel`, `DeviceTelemetryRepository`, `FpsCompatibilityEvaluator`, `ShizukuManager`)
- **Persistence:** Local `PreferencesManager`
