package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.GameScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.GameViewModelFactory

class MainActivity : ComponentActivity() {
  override fun getAttributionTag(): String? {
    return "default"
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val gameViewModel: GameViewModel = viewModel(
          factory = GameViewModelFactory(application)
        )
        GameScreen(viewModel = gameViewModel)
      }
    }
  }
}
