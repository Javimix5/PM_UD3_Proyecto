package com.example.gestionusuarioshibrido.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gestionusuarioshibrido.data.local.User

/**
 * Pantalla de formulario de usuario, que envuelve el contenido en un [Scaffold]
 * con barra superior y navegación hacia atrás.
 *
 * Esta pantalla decide si se muestra el formulario en modo:
 * - Creación: cuando [userId] es `null`
 * - Edición: cuando [userId] contiene el ID de un usuario existente
 *
 * Delegará el contenido editable al composable [UserEditScreen].
 *
 * @param users Lista completa de usuarios, utilizada para obtener el usuario a editar.
 * @param userId ID del usuario a modificar o `null` si se está creando uno nuevo.
 * @param onDone Callback ejecutado cuando el usuario confirma la creación o edición.
 * @param onBack Callback ejecutado cuando se pulsa el botón de retroceso.
 * @param modifier Modificador opcional para ajustar la apariencia del formulario.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFormScreen(
    users: List<User>,
    userId: String?,
    onDone: (User) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Column() {
                    Text(
                        if (userId == null) "Crear Usuario" else "Modificar Usuario",
                    )
                }
            },
                navigationIcon = {
                    IconButton(onClick = {onBack()}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                })
        }
    ) { contentPadding ->
        UserEditScreen(users, userId, onDone, Modifier.padding(contentPadding))
    }
}


/**
 * Pantalla que muestra el formulario editable para crear o modificar un usuario.
 *
 * Si [userId] coincide con un usuario en [users], los campos del formulario se cargan
 * con sus datos actuales; de lo contrario, se muestra un formulario en blanco.
 *
 * Cuando el usuario confirma, se crea una nueva instancia de [User] con los valores
 * actualizados y se envía mediante [onDone].
 *
 * @param users Lista de usuarios existente, usada para obtener datos al editar.
 * @param userId Identificador del usuario a editar o `null` para crear uno nuevo.
 * @param onDone Callback ejecutado al confirmar los cambios del formulario.
 * @param modifier Modificador opcional para ajustar la disposición del formulario.
 */

@Composable
fun UserEditScreen(
    users: List<User>,
    userId: String?,
    onDone: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Buscamos el usuario si estamos en modo edición
    // Usamos 'remember(userId)' para que solo recalcule si cambia el ID
    val existingUser = remember(userId, users) {
        users.find { it.id == userId }
    }

    // 2. Definimos los estados para cada campo del formulario
    // Inicializamos con los datos del usuario existente o cadenas vacías si es nuevo
    var firstName by remember { mutableStateOf(existingUser?.firstName ?: "") }
    var lastName by remember { mutableStateOf(existingUser?.lastName ?: "") }
    var email by remember { mutableStateOf(existingUser?.email ?: "") }
    var age by remember { mutableStateOf(existingUser?.age?.toString() ?: "") }
    var userName by remember { mutableStateOf(existingUser?.userName ?: "") }
    var positionTitle by remember { mutableStateOf(existingUser?.positionTitle ?: "") }
    var imagen by remember { mutableStateOf(existingUser?.imagen ?: "https://randomuser.me/api/portraits/lego/1.jpg") }

    // Estado para controlar validación básica (botón habilitado)
    val isFormValid = firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank()

    // Usamos un Column con scroll vertical por si el teclado tapa los campos
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()), // Habilita scroll
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        OutlinedTextField(
            value = age,
            onValueChange = {
                // Filtramos para que solo acepte números
                if (it.all { char -> char.isDigit() }) age = it
            },
            label = { Text("Edad") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = positionTitle,
            onValueChange = { positionTitle = it },
            label = { Text("Posición / Cargo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = imagen,
            onValueChange = { imagen = it },
            label = { Text("URL Imagen") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2
        )

        Button(
            onClick = {
                // Conversión segura de edad
                val ageInt = age.toIntOrNull() ?: 0

                // LÓGICA CRÍTICA DE IDs
                // Si es nuevo (existingUser es null), generamos ID "local_"
                // Si ya existe, mantenemos su ID original.
                val finalId = existingUser?.id ?: "local_${System.nanoTime()}"

                val userResult = User(
                    id = finalId,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    age = ageInt,
                    userName = userName,
                    positionTitle = positionTitle,
                    imagen = imagen,
                    // pendingSync siempre es true al guardar en local (ver UserRepository)
                    // pero aquí lo dejamos en false porque el Repo se encarga de ponerlo a true
                    // o lo ponemos explícito si queremos asegurar.
                    pendingSync = true,
                    pendingDelete = false
                )

                onDone(userResult)
            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text(if (existingUser == null) "Crear" else "Modificar")
        }
    }
}