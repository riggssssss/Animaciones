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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
    val screens = appScreens //lista de pantallas

    Scaffold(
        bottomBar = {
            // escucha los cambios de estado
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            // Extrae el destino exacto
            val currentDestination = navBackStackEntry?.destination
            
            // Busca en que pantalla estoy. Si no encuentra ninguna, va a Home por defecto
            val currentScreen = screens.find { it.route == currentDestination?.route } ?: ScreenHome

            // Bottom navbar
            AnimatedBottomBar(
                screens = screens,
                currentScreen = currentScreen,
                onTabSelected = { screen ->
                    // Lógica de navegación estándar de Google
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
        // gestiona que pantalla se muestra arriba en función de la ruta
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
    // medir ancho total para calcular cada tab
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val tabWidth = screenWidth / screens.size

    val PaleDogwood = Color(0xFFFFD7D7)
    val Green = Color(0xFFBDFCC9)
    val BlueLight = Color(0xFFD7E8FF)


    // 'updateTransition' coordina todas las animaciones a la vez
    // Observa 'currentScreen' y cuando cambia, avisa a todos los 'animate*' para que se muevan.
    val transition = updateTransition(currentScreen, label = "Tab indicator")

    // El borde IZQUIERDO del indicador
    val indicatorLeft by transition.animateDp(
        transitionSpec = {
            // Aquí está la gracia del efecto elástico:
            if (targetState.route > initialState.route) { 
                // Si voy hacia la DERECHA -> El borde izquierdo se mueve LENTO (parece que se estira)
                spring(stiffness = Spring.StiffnessVeryLow)
            } else {
                // Si voy hacia la IZQUIERDA -> El borde izquierdo corre (parece que se encoge)
                spring(stiffness = Spring.StiffnessMedium)
            }
        },
        label = "Indicator left"
    ) { screen ->

        val index = screens.indexOf(screen)
        tabWidth * index
    }

    // El borde DERECHO del indicador
    val indicatorRight by transition.animateDp(
        transitionSpec = {
            if (targetState.route > initialState.route) {
                // El borde derecho corre
                spring(stiffness = Spring.StiffnessMedium)
            } else {
                // El borde derecho va lento para recuperar forma
                spring(stiffness = Spring.StiffnessVeryLow)
            }
        },
        label = "Indicator right"
    ) { screen ->

        val index = screens.indexOf(screen)
        tabWidth * (index + 1)
    }


    // Cambia suavemente entre colores
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

    // --- DIBUJADO DE LA BARRA ---
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Altura estándar para que quepa todo bien
            .background(Color.White)
    ) {
        // El selector del fondo
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .offset(x = indicatorLeft) // Lo muevo a su sitio
                .width(indicatorRight - indicatorLeft) // Su ancho varía creando el efecto chicle
                .padding(vertical = 12.dp, horizontal = 4.dp) // Un poquito de aire
                .background(indicatorColor, RoundedCornerShape(16.dp)) // Redondito queda mejor
        )

        // Iconos y textos
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            screens.forEach { screen ->
                val selected = screen == currentScreen
                
                // Cada item de la barra
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null // Le quito el efecto ripple por defecto porque ensucia la animación
                        ) { onTabSelected(screen) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val icon = when (screen) {
                        ScreenHome -> Icons.Filled.Home
                        ScreenPage1 -> Icons.Filled.Star
                        ScreenPage2 -> Icons.Filled.Person
                        else -> Icons.Filled.Home
                    }
                    
                    Icon(
                        imageVector = icon,
                        contentDescription = screen.title,
                        tint = if (selected) Color.Black else Color.Gray // Si está seleccionado, negro. Si no, gris.
                    )
                    
                    // Animacion del texto para que aparezca suavemente
                    androidx.compose.animation.AnimatedVisibility(
                        visible = selected,
                        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandHorizontally(),
                        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkHorizontally()
                    ) {
                        Text(
                            text = screen.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 4.dp) // Separo un pelín del icono
                        )
                    }
                }
            }
        }
    }
}