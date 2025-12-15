package com.example.gestionusuarioshibrido.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.gestionusuarioshibrido.data.local.User
import com.example.gestionusuarioshibrido.R

@Composable
fun UserCard(
    user: User,
    onEditUser: (String) -> Unit,
    onDeleteUser: (User) -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = CardDefaults.elevatedShape
    ) {

        Column (
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.Start
        ) {
            Row (horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()) {
                // FOTO DEL USUARIO
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(user.imagen)
                        .crossfade(true)
                        .build(),
                    contentDescription = user.firstName,
                    error = painterResource(id = R.drawable.ic_broken_image),
                    placeholder = painterResource(id = R.drawable.loading_img),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(end = 2.dp)
                        .size(80.dp)
                        .clip(CircleShape)
                )


                Column (modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "${user.firstName} ${user.lastName}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = user.positionTitle,
                        fontSize = 15.sp)

                }

                Spacer(modifier = Modifier.size(2.dp))
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.End ,
                    modifier = Modifier.weight(0.2f)
                ) {
                    IconButton(onClick = { onEditUser(user.id) }) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { onDeleteUser(user) }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Eliminar")
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(12.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Text("Usuario: ", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text(user.userName, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.size(30.dp))
                Row {
                    Text("Edad: ", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text("${user.age} años", fontSize = 15.sp)
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Text("Email: ", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(user.email, fontSize = 14.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserCard() {
    val user = User(
        id = "34",
        email = "julio.perez@agisoftware.com",
        firstName = "Julio",
        lastName = "Pérez Sánchez",
        userName = "jperez",
        age = 40,
        positionTitle = "Android Developer",
        imagen = "https://randomuser.me/api/portraits/men/4.jpg")
    UserCard(user = user,
        modifier = Modifier,
        onEditUser = {},
        onDeleteUser = {})
}
