package com.example.solat

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.solat.ui.navigation.SolatNavHost

@Composable
fun SolatApp() {
    val navController = rememberNavController()

    SolatNavHost(navController)
}
