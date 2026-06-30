package com.yukuza.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable 120Hz smooth animations for supported devices
        window.attributes.preferredDisplayModeId = let {
            val display = display
            display.supportedModes.find { mode ->
                mode.refreshRate >= 120f
            }?.let { mode ->
                mode.modeId
            } ?: 0
        }
        
        enableEdgeToEdge()
        setContent {
            com.yukuza.launcher.ui.theme.YukuzaTheme {
                com.yukuza.launcher.navigation.LauncherNavGraph()
            }
        }
    }
}
