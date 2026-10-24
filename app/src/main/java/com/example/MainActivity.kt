package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.InterceptionEventBus
import com.example.ui.analytics.AnalyticsScreen
import com.example.ui.focus.FocusScreen
import com.example.ui.overlay.MindfulOverlayDialog
import com.example.ui.rooms.RoomsScreen
import com.example.ui.shield.ShieldScreen
import com.example.ui.spec.TechSpecScreen
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.RegainTheme
import com.example.ui.theme.SurfaceDark
import com.example.viewmodel.FocusViewModel

enum class RegainTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    FOCUS("Focus", Icons.Default.AvTimer),
    SHIELD("Shield", Icons.Default.Shield),
    ROOMS("Rooms", Icons.Default.Group),
    ANALYTICS("Analytics", Icons.Default.BarChart),
    SPEC("Tech Spec", Icons.Default.MenuBook)
}

class MainActivity : ComponentActivity() {

    private val focusViewModel: FocusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)

        setContent {
            RegainTheme {
                MainAppContainer(
                    viewModel = focusViewModel,
                    onOpenDMs = {
                        Toast.makeText(this, "Direct Messages Allowed: Safe utility link opened.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.getBooleanExtra("SHOW_OVERLAY", false) == true) {
            val app = intent.getStringExtra("INTERCEPTED_APP") ?: "Instagram"
            val surface = intent.getStringExtra("INTERCEPTED_SURFACE") ?: "Reels"
            InterceptionEventBus.triggerInterception(app, surface, "com.instagram.android")
        }
    }
}

@Composable
fun MainAppContainer(
    viewModel: FocusViewModel,
    onOpenDMs: () -> Unit
) {
    var currentTab by remember { mutableStateOf(RegainTab.FOCUS) }
    val activeInterception by InterceptionEventBus.currentInterception.collectAsState()

    if (activeInterception != null) {
        MindfulOverlayDialog(
            interception = activeInterception!!,
            onDismissToFocus = {
                InterceptionEventBus.dismissInterception()
            },
            onOpenDMsOnly = {
                InterceptionEventBus.dismissInterception()
                onOpenDMs()
            },
            onAllowShortPass = {
                InterceptionEventBus.dismissInterception()
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ObsidianDark,
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("regain_bottom_nav")
            ) {
                RegainTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianDark,
                            selectedTextColor = CyanPrimary,
                            indicatorColor = CyanPrimary,
                            unselectedIconColor = Color(0xFF9CA3AF),
                            unselectedTextColor = Color(0xFF9CA3AF)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                RegainTab.FOCUS -> FocusScreen(viewModel = viewModel)
                RegainTab.SHIELD -> ShieldScreen(viewModel = viewModel)
                RegainTab.ROOMS -> RoomsScreen(viewModel = viewModel)
                RegainTab.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                RegainTab.SPEC -> TechSpecScreen()
            }
        }
    }
}
