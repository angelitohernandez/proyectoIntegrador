package mx.edu.utez.proyectointegrador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import mx.edu.utez.proyectointegrador.navigation.AppNavGraph
import mx.edu.utez.proyectointegrador.ui.theme.ProyectoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as ProyectoApplication).appContainer

        setContent {
            ProyectoTheme {
                AppNavGraph(appContainer = appContainer)
            }
        }
    }
}
