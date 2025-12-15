package com.example.gestionusuarioshibrido

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.gestionusuarioshibrido.ui.theme.GestionUsuariosTheme
import com.example.gestionusuarioshibrido.ui.views.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestionUsuariosTheme() {
                AppNavigation()
            }
        }
    }
}