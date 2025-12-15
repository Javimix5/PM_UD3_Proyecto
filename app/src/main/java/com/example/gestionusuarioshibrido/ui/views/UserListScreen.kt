package com.example.gestionusuarioshibrido.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ReplayCircleFilled
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gestionusuarioshibrido.data.local.User
import com.example.gestionusuarioshibrido.ui.components.UserCard

/**
 * Representa la pantalla principal que muestra una lista de usuarios dentro de un `Scaffold`
 * con barra superior y un botón flotante de acción (FAB).
 *
 * Esta pantalla permite:
 * - Visualizar la lista de usuarios.
 * - Navegar a la pantalla de creación de un nuevo usuario mediante el FAB.
 * - Editar un usuario existente.
 * - Eliminar un usuario de la lista.
 * - Sincronizar los datos con el servidor remoto.
 * - Añadir un usuario de prueba.
 *
 * @param users Lista de usuarios mostrados en la interfaz.
 * @param onAddUser Acción a ejecutar cuando se pulsa el botón flotante para añadir un nuevo usuario.
 * @param onEditUser Acción que recibe el **id** del usuario que se desea editar.
 * @param onDeleteUser Acción que recibe el usuario que se desea eliminar.
 * @param onSync Acción a ejecutar cuando se pulsa el botón de sincronización.
 * @param onAddTestUser Acción a ejecutar cuando se pulsa el botón de añadir un usuario de prueba.
 * @param modifier Permite modificar la apariencia o comportamiento del componente desde el exterior.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    users: List<User>,
    onAddUser: () -> Unit,
    onEditUser: (String) -> Unit,
    onDeleteUser: (User) -> Unit,
    onSync: () -> Unit,
    onAddTestUser: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Lista de usuarios") },
                actions = {
                    IconButton(onClick = onSync) {
                        Icon(
                            imageVector = Icons.Rounded.Sync,
                            contentDescription = "Sincronizar"
                        )
                    }
                    IconButton(onClick = onAddTestUser) {
                        Icon(
                            imageVector = Icons.Rounded.PersonAdd,
                            contentDescription = "Añadir Test User"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddUser) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Crear nuevo usuario"
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (users.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay usuarios.\nUsa el botón + o agita el móvil.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(users) { user ->
                        UserCard(
                            user = user,
                            onEditUser = { onEditUser(user.id) },
                            onDeleteUser = { onDeleteUser(user) }
                        )
                    }
                }
            }
        }
    }
}