package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TelemetryScreen
import com.example.ui.theme.DarkObsidian
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RaunakExploitsTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

enum class AppDestination(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME("Home", Icons.Default.Home),
    TELEMETRY("Telemetry", Icons.Default.Refresh),
    DIAGNOSTICS("Diagnostics", Icons.Default.Build),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RaunakExploitsTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshTelemetry()
    }
}

@Composable
fun MainAppContainer(viewModel: MainViewModel) {
    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                contentColor = TextWhite
            ) {
                AppDestination.entries.forEach { destination ->
                    val isSelected = currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = {
                            Text(
                                text = destination.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_${destination.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentDestination,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ScreenTransition",
            modifier = Modifier.padding(innerPadding)
        ) { destination ->
            when (destination) {
                AppDestination.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToTelemetry = { currentDestination = AppDestination.TELEMETRY },
                    onNavigateToDiagnostics = { currentDestination = AppDestination.DIAGNOSTICS },
                    onNavigateToSettings = { currentDestination = AppDestination.SETTINGS }
                )
                AppDestination.TELEMETRY -> TelemetryScreen(
                    viewModel = viewModel,
                    onBack = { currentDestination = AppDestination.HOME }
                )
                AppDestination.DIAGNOSTICS -> DiagnosticsScreen(
                    viewModel = viewModel,
                    onBack = { currentDestination = AppDestination.HOME }
                )
                AppDestination.SETTINGS -> SettingsScreen(
                    viewModel = viewModel,
                    onBack = { currentDestination = AppDestination.HOME }
                )
            }
        }
    }
}
