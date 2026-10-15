package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.shizuku.ShizukuStatus
import com.example.ui.MainViewModel
import com.example.ui.components.CyberCard
import com.example.ui.components.EvaluationResultCard
import com.example.ui.components.FpsTargetPicker
import com.example.ui.components.GameProfileSelector
import com.example.ui.components.ShizukuStatusPill
import com.example.ui.theme.DarkCrimsonContainer
import com.example.ui.theme.DarkCyanContainer
import com.example.ui.theme.DarkGreenContainer
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.HotCrimson
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToTelemetry: () -> Unit,
    onNavigateToDiagnostics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val telemetry by viewModel.telemetry.collectAsState()
    val shizukuStatus by viewModel.shizukuStatus.collectAsState()
    val selectedGame by viewModel.selectedGame.collectAsState()
    val targetFps by viewModel.targetFps.collectAsState()
    val allow144And165Fps by viewModel.allow144And165Fps.collectAsState()
    val isEvaluating by viewModel.isEvaluating.collectAsState()
    val evaluationResult by viewModel.evaluationResult.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Branding Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "RAUNAK EXPLOITS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonCyan,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "Vivo & iQOO Gaming FPS Architect",
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }

            Surface(
                color = DarkCyanContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).background(MatrixGreen, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${telemetry.currentRefreshRate.toInt()} Hz",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Hero Banner Card with visual aesthetics
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    1.dp,
                    Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.6f), ElectricAmber.copy(alpha = 0.4f))),
                    RoundedCornerShape(16.dp)
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.raunak_hero_banner),
                contentDescription = "RAUNAK EXPLOITS Gaming Engine Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xCC090D14))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Surface(
                    color = if (telemetry.isVivoOrIqoo) DarkGreenContainer else MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (telemetry.isVivoOrIqoo) MatrixGreen else NeonCyan),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (telemetry.isVivoOrIqoo) "VIVO / iQOO HARDWARE VERIFIED" else "GENERIC ANDROID DETECTED",
                        color = if (telemetry.isVivoOrIqoo) MatrixGreen else NeonCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${telemetry.manufacturer} ${telemetry.model} • ${telemetry.androidVersion}",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Shizuku Connection Status Section
        CyberCard(
            modifier = Modifier.testTag("shizuku_card"),
            borderColor = when (shizukuStatus) {
                is ShizukuStatus.Authorized -> MatrixGreen
                is ShizukuStatus.PermissionRequired -> ElectricAmber
                else -> HotCrimson
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SHIZUKU SYSTEM BRIDGE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ShizukuStatusPill(status = shizukuStatus)
                }

                when (shizukuStatus) {
                    is ShizukuStatus.PermissionRequired -> {
                        Button(
                            onClick = { viewModel.requestShizukuPermission() },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricAmber),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("authorize_shizuku_button")
                        ) {
                            Text("Authorize", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    is ShizukuStatus.Authorized -> {
                        Surface(
                            color = DarkGreenContainer,
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Check, contentDescription = "Authorized", tint = MatrixGreen, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                    else -> {
                        OutlinedButton(
                            onClick = { onNavigateToDiagnostics() },
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("shizuku_setup_button")
                        ) {
                            Text("Setup", color = NeonCyan, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Target Game Selector
        GameProfileSelector(
            selectedGame = selectedGame,
            onSelectGame = { viewModel.selectGame(it) }
        )

        // Requested Target FPS Picker
        FpsTargetPicker(
            selectedTarget = targetFps,
            allow144And165 = allow144And165Fps,
            onToggle144And165 = { viewModel.toggleAllow144And165Fps(!allow144And165Fps) },
            onSelectTarget = { viewModel.selectFpsTarget(it) }
        )

        // START & STOP Action Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val isStartEnabled = !isEvaluating

            Button(
                onClick = {
                    if (shizukuStatus is ShizukuStatus.PermissionRequired) {
                        viewModel.requestShizukuPermission()
                    } else if (shizukuStatus !is ShizukuStatus.Authorized) {
                        onNavigateToDiagnostics()
                    } else {
                        viewModel.startEvaluation()
                    }
                },
                enabled = isStartEnabled,
                modifier = Modifier
                    .weight(1.5f)
                    .height(52.dp)
                    .testTag("start_evaluation_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    disabledContainerColor = NeonCyan.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isEvaluating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EVALUATING...",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (shizukuStatus is ShizukuStatus.Authorized) "START EVALUATION" else "CONNECT & START",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            OutlinedButton(
                onClick = { viewModel.stopEvaluation() },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("stop_evaluation_button"),
                border = androidx.compose.foundation.BorderStroke(1.dp, HotCrimson),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = DarkCrimsonContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    tint = HotCrimson,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "RESET",
                    color = HotCrimson,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Evaluation Result Card (Animated reveal)
        AnimatedVisibility(
            visible = evaluationResult != null,
            enter = fadeIn() + slideInVertically()
        ) {
            evaluationResult?.let { result ->
                EvaluationResultCard(
                    result = result,
                    onLaunchGame = { viewModel.launchGame(context, result.game) },
                    onViewDiagnostics = onNavigateToDiagnostics
                )
            }
        }

        // Quick Navigation Tiles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NavigationTile(
                title = "Telemetry",
                icon = Icons.Default.Refresh,
                onClick = onNavigateToTelemetry,
                modifier = Modifier.weight(1f).testTag("nav_tile_telemetry")
            )
            NavigationTile(
                title = "Diagnostics",
                icon = Icons.Default.Build,
                onClick = onNavigateToDiagnostics,
                modifier = Modifier.weight(1f).testTag("nav_tile_diagnostics")
            )
            NavigationTile(
                title = "Settings",
                icon = Icons.Default.Settings,
                onClick = onNavigateToSettings,
                modifier = Modifier.weight(1f).testTag("nav_tile_settings")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun NavigationTile(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = NeonCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = TextWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
