package com.example.gestionusuarioshibrido.ui.views

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestionusuarioshibrido.viewmodel.UserViewModel


/**
 * Controlador principal de navegación de la aplicación.
 *
 * Este composable centraliza todas las rutas y pantallas, gestionando:
 * - La creación del `NavController`.
 * - El acceso al `UserViewModel`.
 * - La configuración del shake listener.
 * - La configuración del `SnackbarHostState` para mostrar mensajes al usuario.
 * - La definición del `NavHost` y sus destinos.
 *
 * Pantallas incluidas:
 *  - `"user_list"`: Lista de usuarios.
 *  - `"user_form"`: Formulario para crear un usuario.
 *  - `"user_form/{id}"`: Formulario para editar un usuario existente.
 *
 * La navegación se realiza mediante rutas simples con argumentos.
 *
 * @param modifier Modificador opcional para ajustar el contenedor raíz.
 */

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    // Obtenemos el Contexto
    val context = LocalContext.current

    // Controlador de navegación
    val navController = rememberNavController()

    // ViewModel de usuarios
    val userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory)
    val users by userViewModel.users.collectAsState()

    // Iniciamos el coordinador del sensor en el UserViewModel
    // Este se detendrá automáticamente cuando el ViewModel se destruya (onCleared)
    userViewModel.setupShakeListener(context)

    // Implementamos un SnackbarHostState para mostrar mensajes al usuario
    // Se mostrarán mensajes a medida que el ViewModel los envíe
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarHostState) {
        userViewModel.events.collect { message ->
            snackbarHostState.showSnackbar(message = message,duration = SnackbarDuration.Short)
        }
    }

    Scaffold(
        snackbarHost = {SnackbarHost(hostState = snackbarHostState)},
        modifier = modifier
    ) { paddingValues ->
        val paddingModifier = Modifier.padding(paddingValues)
        NavHost(
            navController = navController, startDestination = "user_list"
        ) {

            // ---------------------------
            // Pantalla: Lista de usuarios
            // ---------------------------
            composable(route = "user_list") {
                throw UnsupportedOperationException("A completar por el estudiante")
            }

            // ----------------------------------------
            // Pantalla: Formulario para crear usuario
            // ----------------------------------------
            composable(route = "user_form") {
                throw UnsupportedOperationException("A completar por el estudiante")
            }

            // ----------------------------------------
            // Pantalla: Formulario para editar usuario
            // ----------------------------------------
            composable(
                route = "user_form/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                throw UnsupportedOperationException("A completar por el estudiante")
            }
        }
    }
}
