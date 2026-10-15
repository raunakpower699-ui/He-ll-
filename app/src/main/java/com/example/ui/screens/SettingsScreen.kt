package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.CyberCard
import com.example.ui.components.FpsTargetPicker
import com.example.ui.components.GameProfileSelector
import com.example.ui.theme.DarkGreenContainer
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val selectedGame by viewModel.selectedGame.collectAsState()
    val targetFps by viewModel.targetFps.collectAsState()
    val allow144And165Fps by viewModel.allow144And165Fps.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SETTINGS & ARCHITECTURE",
                        color = NeonCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Default Game & FPS Configuration
            CyberCard(borderColor = NeonCyan) {
                Text(
                    text = "DEFAULT PROFILE PREFERENCES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                GameProfileSelector(
                    selectedGame = selectedGame,
                    onSelectGame = { viewModel.selectGame(it) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                FpsTargetPicker(
                    selectedTarget = targetFps,
                    allow144And165 = allow144And165Fps,
                    onToggle144And165 = { viewModel.toggleAllow144And165Fps(!allow144And165Fps) },
                    onSelectTarget = { viewModel.selectFpsTarget(it) }
                )
            }

            // Vivo & iQOO Tuning Knowledge Base
            CyberCard(borderColor = ElectricAmber) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = ElectricAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VIVO & iQOO OS BEHAVIORS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricAmber,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                TuningNote(
                    "FuntouchOS / OriginOS 60Hz Game Throttling",
                    "By default, Vivo's OS forces display scanout down to 60Hz when games launch to conserve battery. RAUNAK EXPLOITS utilizes Shizuku to pin 'peak_refresh_rate', ensuring the display panel stays at 90Hz/120Hz/144Hz."
                )

                TuningNote(
                    "Monster Mode / Ultra Game Mode",
                    "On iQOO smartphones, enabling Monster Mode in Quick Settings unthrottles thermal governors and unlocks maximum clock frequencies on performance cores."
                )

                TuningNote(
                    "App-Specific Screen Refresh Rate",
                    "In phone Settings > Display & Brightness > Screen refresh rate > App-specific refresh rate, ensure BGMI and Free Fire are set to 'High refresh rate' or '120Hz'."
                )
            }

            // Anti-Cheat & Ethical Gaming Policy
            CyberCard(borderColor = MatrixGreen) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MatrixGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ANTI-CHEAT & FAIR PLAY COMPLIANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MatrixGreen,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "RAUNAK EXPLOITS is built strictly for ethical device capability diagnostics and system-level display refresh configuration. We strictly uphold:",
                    color = TextWhite,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                ComplianceBullet("No Game File Modification: Zero tampering with game OBB, APK, UserCustom.ini, or private data.")
                ComplianceBullet("No Memory Injection: No hooking into game processes, avoiding Tencent ACE, BattlEye, and Garena detection.")
                ComplianceBullet("No Spoofed FPS: We never report fake frame rates or claim unsupported FPS modes are unlocked.")
                ComplianceBullet("Rootless & Transparent: Uses official Android Shizuku APIs running as the authorized shell UID.")
            }

            // About & Credits
            CyberCard(borderColor = MaterialTheme.colorScheme.outline) {
                Text(
                    text = "ABOUT RAUNAK EXPLOITS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                AboutRow("Version", "1.0.0 (Release 2026)")
                AboutRow("Author / Lead", "RAUNAK")
                AboutRow("Shizuku API Version", "13.1.5 (Rikka)")
                AboutRow("Target Systems", "Vivo (FuntouchOS) & iQOO (OriginOS)")
                AboutRow("Architecture", "Kotlin • Jetpack Compose • M3")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TuningNote(title: String, body: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = title, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text(text = body, color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
    }
}

@Composable
private fun ComplianceBullet(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = "• ", color = MatrixGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(text = text, color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextMuted, fontSize = 11.sp)
        Text(text = value, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
