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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.CyberCard
import com.example.ui.theme.DarkCyanContainer
import com.example.ui.theme.ElectricAmber
import com.example.ui.theme.HotCrimson
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelemetryScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val telemetry by viewModel.telemetry.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "HARDWARE TELEMETRY",
                        color = NeonCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("telemetry_back_button")
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
                        onClick = { viewModel.refreshTelemetry() },
                        modifier = Modifier.testTag("telemetry_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Telemetry",
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
            // Live Display Scanout & Modes Card
            CyberCard(borderColor = NeonCyan) {
                Text(
                    text = "DISPLAY SCANOUT & MODES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Active Refresh Rate", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = "${telemetry.currentRefreshRate.toInt()} Hz",
                            color = MatrixGreen,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Max Panel Capability", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = "${telemetry.maxSupportedRefreshRate.toInt()} Hz",
                            color = NeonCyan,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Available Hardware Modes:", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))

                if (telemetry.supportedModes.isEmpty()) {
                    Text(text = "No additional modes reported by DisplayManager", color = TextMuted, fontSize = 11.sp)
                } else {
                    telemetry.supportedModes.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Mode #${mode.modeId}: ${mode.width} x ${mode.height}",
                                color = TextWhite,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${mode.refreshRate.toInt()} Hz",
                                color = if (mode.refreshRate >= 120f) MatrixGreen else NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Vivo / iQOO Platform Details
            CyberCard(borderColor = ElectricAmber) {
                Text(
                    text = "DEVICE PLATFORM & OEM PROFILE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricAmber,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                TelemetryRow("Manufacturer", telemetry.manufacturer)
                TelemetryRow("Brand & Model", "${telemetry.brand} ${telemetry.model}")
                TelemetryRow("OS Version", "${telemetry.androidVersion} (API ${telemetry.apiLevel})")
                TelemetryRow("Vivo / iQOO Hardware", if (telemetry.isVivoOrIqoo) "Detected & Optimized" else "Non-Vivo Device")
                telemetry.vivoOsVersion?.let {
                    TelemetryRow("Vivo OS Build", it)
                }
            }

            // Real RAM & Memory
            CyberCard(borderColor = MaterialTheme.colorScheme.outline) {
                Text(
                    text = "SYSTEM MEMORY (RAM)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                val usedRam = (telemetry.totalRamMb - telemetry.availableRamMb).coerceAtLeast(0)
                val ramFraction = if (telemetry.totalRamMb > 0) usedRam.toFloat() / telemetry.totalRamMb else 0f

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Allocated: ${usedRam} MB", color = TextWhite, fontSize = 12.sp)
                    Text(text = "Total: ${telemetry.totalRamMb} MB", color = TextMuted, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { ramFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (ramFraction > 0.85f) HotCrimson else NeonCyan,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                TelemetryRow("Available Free RAM", "${telemetry.availableRamMb} MB")
                TelemetryRow("Low Memory State", if (telemetry.isLowRam) "Active Alert" else "Healthy")
            }

            // Battery & Thermals
            CyberCard(borderColor = MatrixGreen) {
                Text(
                    text = "THERMALS & POWER TELEMETRY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MatrixGreen,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                TelemetryRow("Battery Charge", if (telemetry.batteryLevel >= 0) "${telemetry.batteryLevel}%" else "N/A")
                TelemetryRow("Battery Temp", if (telemetry.batteryTemperatureC > 0) "${telemetry.batteryTemperatureC} °C" else "N/A")
                TelemetryRow("Power State", telemetry.batteryStatus)
                TelemetryRow("Thermal Status", telemetry.thermalStatus)
                telemetry.thermalHeadroom?.let {
                    TelemetryRow("Thermal Headroom", String.format("%.2f (Healthy > 1.0)", it))
                }
            }

            // CPU Architecture
            CyberCard(borderColor = MaterialTheme.colorScheme.outline) {
                Text(
                    text = "PROCESSOR & CPU CORES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                TelemetryRow("CPU Architecture", telemetry.cpuArchitecture)
                TelemetryRow("Active CPU Cores", "${telemetry.cpuCores} physical cores")
                TelemetryRow("Chipset Identifier", telemetry.cpuHardwareName ?: "N/A")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TelemetryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextMuted, fontSize = 12.sp)
        Text(
            text = value,
            color = TextWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
