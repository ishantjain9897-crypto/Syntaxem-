package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.core.designsystem.CalcMasterTheme
import com.example.core.designsystem.SyntaxemThemeManager
import com.example.syntaxem.ui.SyntaxemMainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SyntaxemThemeManager.init(this)
        enableEdgeToEdge()
        setContent {
            CalcMasterTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SyntaxemMainScreen()
                }
            }
        }
    }
}
