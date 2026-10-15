package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shizuku.ShizukuStatus
import com.example.ui.MainViewModel
import com.example.ui.components.CyberCard
import com.example.ui.components.ShizukuStatusPill
import com.example.ui.theme.DarkCyanContainer
import com.example.ui.theme.DarkGreenContainer
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.HotCrimson
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val shizukuStatus by viewModel.shizukuStatus.collectAsState()
    val logs by viewModel.diagnosticLogs.collectAsState()
    val shellOutput by viewModel.shellOutput.collectAsState()

    var commandInput by remember { mutableStateOf("settings get system peak_refresh_rate") }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DIAGNOSTICS & SHIZUKU BRIDGE",
                        color = NeonCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("diag_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonCyan
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.clearLogs() },
                        modifier = Modifier.testTag("clear_logs_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Logs",
                            tint = TextMuted
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
            // Shizuku Status Overview
            CyberCard(borderColor = NeonCyan) {
                Text(
                    text = "SHIZUKU BINDER & PERMISSION AUDIT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                ShizukuStatusPill(status = shizukuStatus)
                Spacer(modifier = Modifier.height(8.dp))

                when (shizukuStatus) {
                    is ShizukuStatus.Authorized -> {
                        Text(
                            text = "Elevated ADB shell execution is active. RAUNAK EXPLOITS is authorized to read and configure system-level display scanout parameters without Root.",
                            color = TextWhite,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                    is ShizukuStatus.PermissionRequired -> {
                        Text(
                            text = "Shizuku service is running, but permission has not been granted yet. Tap below to prompt authorization.",
                            color = ElectricAmber,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.requestShizukuPermission() },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricAmber),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("diag_request_permission")
                        ) {
                            Text("Grant Permission", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                    else -> {
                        Text(
                            text = "Shizuku service is not active. Follow the setup guide below to launch Shizuku via Wireless Debugging (No PC required on Android 11+).",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Shizuku Setup Guide (when not authorized)
            if (shizukuStatus !is ShizukuStatus.Authorized) {
                CyberCard(borderColor = ElectricAmber) {
                    Text(
                        text = "HOW TO START SHIZUKU (NON-ROOT)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricAmber,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    SetupStep(1, "Install Shizuku", "Get the official Shizuku app from GitHub or Google Play Store.")
                    SetupStep(2, "Enable Developer Options", "Go to Settings > About Phone > Tap 'Build Number' 7 times.")
                    SetupStep(3, "Wireless Debugging (Android 11+)", "In Developer Options, turn on 'Wireless Debugging'. Split screen or use notification pairing.")
                    SetupStep(4, "Pair & Start in Shizuku", "Open Shizuku > Tap 'Pairing' > Enter the 6-digit code > Tap 'Start'.")
                    SetupStep(5, "Via PC (Alternative)", "Run ADB: adb shell sh /sdcard/Android/data/moe.shizuku.privileged.api/start.sh")
                }
            }

            // Shell Command Tester
            CyberCard(borderColor = MaterialTheme.colorScheme.outline) {
                Text(
                    text = "SHIZUKU SHELL EXECUTION TESTER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QuickCommandChip("id", "Check UID") { commandInput = "id" }
                    QuickCommandChip("peak_rate", "Peak Hz") { commandInput = "settings get system peak_refresh_rate" }
                    QuickCommandChip("model", "Device Model") { commandInput = "getprop ro.product.model" }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = commandInput,
                        onValueChange = { commandInput = it },
                        modifier = Modifier.weight(1f).testTag("shell_command_input"),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        placeholder = { Text("Enter command...", color = TextMuted, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        singleLine = true
                    )

                    Button(
                        onClick = { viewModel.runShellTest(commandInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(8.dp),
                        enabled = shizukuStatus is ShizukuStatus.Authorized,
                        modifier = Modifier.testTag("shell_run_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = Color.Black)
                    }
                }

                if (shellOutput.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color(0xFF070A0F),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2838)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "TERMINAL OUTPUT:", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = shellOutput,
                                color = MatrixGreen,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Real-Time Activity Logs
            CyberCard(borderColor = MaterialTheme.colorScheme.outline) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVITY & EVALUATION LOGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${logs.size} entries",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = Color(0xFF070A0F),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2838)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    if (logs.isEmpty()) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text("No logs recorded yet", color = TextMuted, fontSize = 11.sp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            items(logs) { log ->
                                Text(
                                    text = log,
                                    color = if (log.contains("Error", ignoreCase = true) || log.contains("failed", ignoreCase = true)) HotCrimson
                                    else if (log.contains("verified", ignoreCase = true) || log.contains("succeeded", ignoreCase = true)) MatrixGreen
                                    else TextWhite.copy(alpha = 0.85f),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 14.sp,
                                    modifier = Modifier.padding(vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SetupStep(stepNumber: Int, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            color = DarkCyanContainer,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.size(20.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "$stepNumber",
                    color = NeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = description, color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
        }
    }
}

@Composable
private fun QuickCommandChip(label: String, tooltip: String, onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            color = NeonCyan,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
