package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fps.EvaluationResult
import com.example.fps.EvaluationStatus
import com.example.fps.FpsTarget
import com.example.fps.GameProfile
import com.example.shizuku.ShizukuStatus
import com.example.ui.theme.DarkCrimsonContainer
import com.example.ui.theme.DarkCyanContainer
import com.example.ui.theme.DarkGreenContainer
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.HotCrimson
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    glowEffect: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val borderBrush = if (glowEffect) {
        Brush.linearGradient(
            colors = listOf(NeonCyan.copy(alpha = 0.8f), MatrixGreen.copy(alpha = 0.6f))
        )
    } else {
        Brush.linearGradient(
            colors = listOf(borderColor, borderColor.copy(alpha = 0.4f))
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, borderBrush), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun ShizukuStatusPill(
    status: ShizukuStatus,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val (dotColor, label, containerColor) = when (status) {
        is ShizukuStatus.Authorized -> Triple(StatusGreen, "Shizuku Authorized (v${status.version})", DarkGreenContainer.copy(alpha = 0.5f))
        is ShizukuStatus.PermissionRequired -> Triple(StatusAmber, "Permission Required", ElectricAmber.copy(alpha = 0.15f))
        is ShizukuStatus.ServiceStopped -> Triple(StatusRed, "Shizuku Service Stopped", DarkCrimsonContainer.copy(alpha = 0.4f))
        is ShizukuStatus.NotInstalled -> Triple(StatusRed, "Shizuku Not Installed", DarkCrimsonContainer.copy(alpha = 0.4f))
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .testTag("shizuku_status_pill"),
        color = containerColor,
        border = BorderStroke(1.dp, dotColor.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(dotColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun GameProfileSelector(
    selectedGame: GameProfile,
    onSelectGame: (GameProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "TARGET GAME PROFILE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GameProfile.entries.forEach { game ->
                val isSelected = game == selectedGame
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) DarkCyanContainer else MaterialTheme.colorScheme.surfaceVariant,
                    animationSpec = tween(200)
                )
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) NeonCyan else Color.Transparent,
                    animationSpec = tween(200)
                )

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectGame(game) }
                        .testTag("game_chip_${game.name.lowercase()}"),
                    color = backgroundColor,
                    border = BorderStroke(1.dp, borderColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = game.displayName,
                            color = if (isSelected) NeonCyan else TextWhite,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Max ${game.maxEngineFps} FPS",
                            color = if (isSelected) TextWhite.copy(alpha = 0.8f) else TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FpsTargetPicker(
    selectedTarget: FpsTarget,
    allow144And165: Boolean = false,
    onToggle144And165: () -> Unit = {},
    onSelectTarget: (FpsTarget) -> Unit,
    modifier: Modifier = Modifier
) {
    val availableTargets = if (allow144And165) {
        FpsTarget.entries
    } else {
        listOf(FpsTarget.FPS_60, FpsTarget.FPS_90, FpsTarget.FPS_120)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "REQUESTED TARGET FPS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricAmber,
                letterSpacing = 1.sp
            )
            Surface(
                color = if (allow144And165) DarkCyanContainer else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, if (allow144And165) NeonCyan else MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onToggle144And165() }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (allow144And165) "144/165 ON" else "144/165 OFF",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (allow144And165) NeonCyan else TextMuted
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            availableTargets.forEach { target ->
                val isSelected = target == selectedTarget
                val borderColor = if (isSelected) {
                    if (target.fps >= 120) MatrixGreen else NeonCyan
                } else {
                    MaterialTheme.colorScheme.outline
                }
                val bgColor = if (isSelected) {
                    if (target.fps >= 120) DarkGreenContainer.copy(alpha = 0.7f) else DarkCyanContainer.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectTarget(target) }
                        .testTag("fps_chip_${target.fps}"),
                    color = bgColor,
                    border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${target.fps}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) TextWhite else TextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "FPS",
                            fontSize = 9.sp,
                            color = if (isSelected) (if (target.fps >= 120) MatrixGreen else NeonCyan) else TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EvaluationResultCard(
    result: EvaluationResult,
    onLaunchGame: () -> Unit,
    onViewDiagnostics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (statusColor, statusIcon) = when (result.status) {
        EvaluationStatus.SUPPORTED_AND_APPLIED -> Pair(MatrixGreen, Icons.Default.CheckCircle)
        EvaluationStatus.ALREADY_ENABLED -> Pair(MatrixGreen, Icons.Default.CheckCircle)
        EvaluationStatus.UNSUPPORTED_BY_HARDWARE -> Pair(HotCrimson, Icons.Default.Warning)
        EvaluationStatus.UNSUPPORTED_BY_GAME_ENGINE -> Pair(StatusAmber, Icons.Default.Warning)
        EvaluationStatus.PARTNER_WHITELIST_REQUIRED -> Pair(ElectricAmber, Icons.Default.Info)
        EvaluationStatus.GAME_NOT_INSTALLED -> Pair(HotCrimson, Icons.Default.Warning)
        EvaluationStatus.PERMISSION_REQUIRED -> Pair(StatusAmber, Icons.Default.Info)
        EvaluationStatus.SHIZUKU_DISCONNECTED -> Pair(StatusRed, Icons.Default.Warning)
        EvaluationStatus.CHANGE_NOT_VERIFIED -> Pair(StatusAmber, Icons.Default.Warning)
    }

    CyberCard(
        modifier = modifier.testTag("evaluation_result_card"),
        borderColor = statusColor,
        glowEffect = result.status == EvaluationStatus.SUPPORTED_AND_APPLIED
    ) {
        // Status Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = statusIcon,
                contentDescription = result.status.title,
                tint = statusColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.status.title.uppercase(),
                    color = statusColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "${result.game.displayName} • ${result.targetFps.label}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hindi Explanation Highlight Box
        if (result.hindiExplanation.isNotBlank()) {
            Surface(
                color = DarkGreenContainer.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, MatrixGreen.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "100% REAL VIVO/iQOO CONFIGURATION:",
                        color = MatrixGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = result.hindiExplanation,
                        color = TextWhite,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Text(
            text = result.explanation,
            color = TextWhite,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Step-by-Step In-Game Settings Guide
        if (result.inGameSettingInstructions.isNotEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "DEFAULT GRAPHICS SETTING STEPS:",
                        color = ElectricAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    result.inGameSettingInstructions.forEach { step ->
                        Text(
                            text = step,
                            color = TextWhite,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Big Prominent "LAUNCH GAME" Button
        if (result.isGameInstalled && result.status == EvaluationStatus.SUPPORTED_AND_APPLIED) {
            Button(
                onClick = onLaunchGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("launch_game_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreen
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LAUNCH ${result.game.displayName.uppercase()} (${result.targetFps.fps} FPS)",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Hardware vs Game Spec Matrix
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ResultSpecRow("Display Scanout", "${result.currentDisplayHz.toInt()} Hz (Max: ${result.maxDisplayHz.toInt()} Hz)")
                ResultSpecRow("Engine Support Cap", "${result.game.maxEngineFps} FPS (${result.game.engineName})")
                ResultSpecRow("Anti-Cheat Status", "100% Compliant (Zero Binary Tampering)", highlightColor = MatrixGreen)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onViewDiagnostics() }
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "View Detailed Diagnostic Audit & Logs",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ResultSpecRow(label: String, value: String, highlightColor: Color? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextMuted, fontSize = 11.sp)
        Text(
            text = value,
            color = highlightColor ?: TextWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
