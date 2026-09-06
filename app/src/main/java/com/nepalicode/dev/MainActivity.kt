package com.nepalicode.dev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.nepalicode.dev.nepalicode.ui.MainIdeScreen
import com.nepalicode.dev.nepalicode.ui.NepaliCodeViewModel
import com.nepalicode.dev.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: NepaliCodeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainIdeScreen(viewModel = viewModel)
            }
        }
    }
}
