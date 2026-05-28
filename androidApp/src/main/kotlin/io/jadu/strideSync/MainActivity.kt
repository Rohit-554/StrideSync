package io.jadu.strideSync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = 0xFF111318.toInt()
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = 0xFF1C1F26.toInt()
            )
        )
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}
