package com.example.animaciones

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Representa una pantalla en la navegación.
 * Usamos una 'data class' simple para que sea más fácil de entender.
 */
data class ScreenModel(val route: String, val title: String)

// Definimos las pantallas como variables globales simples
val ScreenHome = ScreenModel("home", "Home")
val ScreenPage1 = ScreenModel("page1", "Page 1")
val ScreenPage2 = ScreenModel("page2", "Page 2")

// Lista de todas las pantallas para la barra de navegación
val appScreens = listOf(ScreenHome, ScreenPage1, ScreenPage2)

@Composable
fun HomeScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Home Screen")
    }
}

@Composable
fun Page1Screen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Page 1 Screen")
    }
}

@Composable
fun Page2Screen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Page 2 Screen")
    }
}
