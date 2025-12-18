package com.example.gestionusuarioshibrido.ui.views

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
 */

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val navController = rememberNavController()

    val userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory)
    val users by userViewModel.users.collectAsState()

    userViewModel.setupShakeListener(context)

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarHostState) {
        userViewModel.events.collect { message ->
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                UserListScreen(
                    users = users,
                    onAddUser = {
                        navController.navigate("user_form")
                    },
                    onEditUser = { id: String ->
                        // Navegar al formulario de edición pasando el ID
                        navController.navigate("user_form/" + id)
                    },
                    onDeleteUser = { user ->
                        userViewModel.deleteUser(user)
                    },
                    onSync = {
                        userViewModel.sync()
                    },
                    onAddTestUser = {
                        userViewModel.addTestUser()
                    },
                    modifier = paddingModifier
                )
            }

            // ----------------------------------------
            // Pantalla: Formulario para crear usuario
            // ----------------------------------------
            composable(route = "user_form") {
                UserFormScreen(
                    users = users,
                    userId = null,
                    onDone = { newUser ->
                        userViewModel.insertUser(newUser)
                        navController.popBackStack()
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                    modifier = paddingModifier
                )
            }

            // ----------------------------------------
            // Pantalla: Formulario para editar usuario
            // ----------------------------------------
            composable(
                route = "user_form/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                val userToEdit = users.find { it.id == id }

                if (userToEdit != null) {
                    UserFormScreen(
                        users = users,
                        userId = id,
                        onDone = { updatedUser ->
                            userViewModel.updateUser(updatedUser)
                            navController.popBackStack()
                        },
                        onBack = {
                            navController.popBackStack()
                        },
                        modifier = paddingModifier
                    )
                } else {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                }
            }
        }
    }
}
