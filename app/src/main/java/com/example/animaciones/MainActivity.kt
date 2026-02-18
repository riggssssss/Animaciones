package com.example.animaciones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.animaciones.ui.theme.AnimacionesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimacionesTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    // Usamos la lista global definida en Screens.kt
    val screens = appScreens

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Determinamos la pantalla seleccionada actualmente para la animación
            val currentScreen = screens.find { it.route == currentDestination?.route } ?: ScreenHome

            AnimatedBottomBar(
                screens = screens,
                currentScreen = currentScreen,
                onTabSelected = { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ScreenHome.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ScreenHome.route) { HomeScreen() }
            composable(ScreenPage1.route) { Page1Screen() }
            composable(ScreenPage2.route) { Page2Screen() }
        }
    }
}


@Composable
fun AnimatedBottomBar(
    screens: List<ScreenModel>,
    currentScreen: ScreenModel,
    onTabSelected: (ScreenModel) -> Unit
) {
    // Calculamos el ancho de cada pestaña dividiendo el ancho total de la pantalla por el número de items
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val tabWidth = screenWidth / screens.size

    // Colores para la animación
    val PaleDogwood = Color(0xFFFFD7D7)
    val Green = Color(0xFFBDFCC9)
    val BlueLight = Color(0xFFD7E8FF) // Un tercer color para darle variedad

    // TRANSICIÓN PRINCIPAL
    // Usamos updateTransition para sincronizar todas las animaciones cuando cambia el estado 'currentScreen'
    val transition = updateTransition(currentScreen, label = "Tab indicator")

    // Animación del borde IZQUIERDO del indicador
    val indicatorLeft by transition.animateDp(
        transitionSpec = {
            if (targetState.route > initialState.route) { // Nota: Comparación simple de strings para dirección
                 // Si vamos a la derecha -> Borde izquierdo se mueve LENTO (efecto estirar)
                spring(stiffness = Spring.StiffnessVeryLow)
            } else {
                // Si vamos a la izquierda -> Borde izquierdo se mueve RÁPIDO (efecto contraer)
                spring(stiffness = Spring.StiffnessMedium)
            }
        },
        label = "Indicator left"
    ) { screen ->
        // Calculamos la posición del borde izquierdo según el índice de la pantalla
        val index = screens.indexOf(screen)
        tabWidth * index
    }

    // Animación del borde DERECHO del indicador
    val indicatorRight by transition.animateDp(
        transitionSpec = {
            if (targetState.route > initialState.route) {
                // Si vamos a la derecha -> Borde derecho se mueve RÁPIDO (efecto estirar)
                spring(stiffness = Spring.StiffnessMedium)
            } else {
                // Si vamos a la izquierda -> Borde derecho se mueve LENTO (efecto contraer)
                spring(stiffness = Spring.StiffnessVeryLow)
            }
        },
        label = "Indicator right"
    ) { screen ->
        // Posición del borde derecho
        val index = screens.indexOf(screen)
        tabWidth * (index + 1)
    }

    // Animación de COLOR del indicador
    val indicatorColor by transition.animateColor(
        label = "Indicator color"
    ) { screen ->
        when (screen) {
            ScreenHome -> PaleDogwood
            ScreenPage1 -> Green
            ScreenPage2 -> BlueLight
            else -> Color.Gray
        }
    }

    // Contenedor de la barra
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Altura estándar aproximada
            .background(Color.White)
    ) {
        // 1. DIBUJAMOS EL INDICADOR ANIMADO (Detrás de los iconos)
        // Usamos las propiedades animadas 'indicatorLeft' y 'indicatorRight' para definir su posición y tamaño
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .offset(x = indicatorLeft)
                .width(indicatorRight - indicatorLeft) // El ancho cambia dinámicamente creando el efecto elástico
                .padding(vertical = 12.dp, horizontal = 4.dp) // Un poco de margen
                .background(indicatorColor, RoundedCornerShape(16.dp))
        )

        // 2. DIBUJAMOS LOS ITEMS DE NAVEGACIÓN (Sobre el indicador)
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            screens.forEach { screen ->
                val selected = screen == currentScreen
                
                // Item individual
                Column(
                    modifier = Modifier
                        .weight(1f) // Cada item ocupa el mismo espacio
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null // Sin efecto ripple por defecto para que luzca limpio
                        ) { onTabSelected(screen) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Icono
                    val icon = when (screen) {
                        ScreenHome -> Icons.Filled.Home
                        ScreenPage1 -> Icons.Filled.Star
                        ScreenPage2 -> Icons.Filled.Person
                        else -> Icons.Filled.Home
                    }
                    
                    Icon(
                        imageVector = icon,
                        contentDescription = screen.title,
                        tint = if (selected) Color.Black else Color.Gray // Cambio de color al seleccionar
                    )
                    
                    // Animación de visibilidad del texto: Aparece progresivamente
                    androidx.compose.animation.AnimatedVisibility(
                        visible = selected,
                        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandHorizontally(),
                        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkHorizontally()
                    ) {
                        Text(
                            text = screen.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 4.dp) // Un poco de espacio entre icono y texto
                        )
                    }
                }
            }
        }
    }
}