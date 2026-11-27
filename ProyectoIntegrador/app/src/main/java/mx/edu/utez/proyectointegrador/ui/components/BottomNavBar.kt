package mx.edu.utez.proyectointegrador.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import mx.edu.utez.proyectointegrador.data.model.Rol

enum class MainTab(val label: String, val icon: ImageVector) {
    INICIO("Inicio", Icons.Filled.Home),
    AGREGAR("Agregar", Icons.Filled.AddCircle),
    PRODUCTOS("Productos", Icons.Filled.Inventory2),
    MERMA("Merma", Icons.Filled.DeleteSweep),
    ESTADISTICAS("Estadísticas", Icons.Filled.BarChart)
}

fun tabsPara(rol: Rol): List<MainTab> = when (rol) {
    Rol.ADMIN -> listOf(MainTab.INICIO, MainTab.AGREGAR, MainTab.PRODUCTOS, MainTab.MERMA, MainTab.ESTADISTICAS)
    Rol.ENCARGADO -> listOf(MainTab.INICIO, MainTab.AGREGAR, MainTab.PRODUCTOS)
    Rol.VENDEDOR -> listOf(MainTab.INICIO, MainTab.MERMA)
}

@Composable
fun BottomNavBar(
    tabs: List<MainTab>,
    selected: MainTab,
    onSelected: (MainTab) -> Unit
) {
    NavigationBar {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = selected == tab,
                onClick = { onSelected(tab) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) }
            )
        }
    }
}
