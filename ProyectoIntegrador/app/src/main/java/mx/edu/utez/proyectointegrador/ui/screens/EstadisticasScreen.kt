package mx.edu.utez.proyectointegrador.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mx.edu.utez.proyectointegrador.ui.components.BarRow
import mx.edu.utez.proyectointegrador.ui.components.ScreenHeader
import mx.edu.utez.proyectointegrador.ui.components.SectionTitle
import mx.edu.utez.proyectointegrador.ui.components.StatTile
import mx.edu.utez.proyectointegrador.ui.theme.Amber600
import mx.edu.utez.proyectointegrador.ui.theme.Indigo600
import mx.edu.utez.proyectointegrador.ui.theme.Orange600
import mx.edu.utez.proyectointegrador.ui.theme.Red600
import mx.edu.utez.proyectointegrador.ui.theme.Teal700
import mx.edu.utez.proyectointegrador.ui.viewmodels.InventarioUiState
import mx.edu.utez.proyectointegrador.util.formatoMoneda
import mx.edu.utez.proyectointegrador.util.porCaducar

@Composable
fun EstadisticasScreen(state: InventarioUiState, modifier: Modifier = Modifier) {
    val productos = state.productos
    val mermas = state.mermas

    val unidades = productos.sumOf { it.stock }
    val valorInventario = productos.sumOf { it.stock * it.precio }
    val costoMerma = mermas.sumOf { it.cantidad * it.productoPrecio }
    val porCaducar = productos.count { it.porCaducar }

    val mermaPorMotivo = mermas.groupBy { it.motivo }
        .mapValues { entrada -> entrada.value.sumOf { it.cantidad } }
        .entries.sortedByDescending { it.value }
    val stockPorCategoria = productos.groupBy { it.categoria }
        .mapValues { entrada -> entrada.value.sumOf { it.stock } }
        .entries.sortedByDescending { it.value }.take(6)
    val topMerma = mermas.groupBy { it.productoNombre }
        .mapValues { entrada -> entrada.value.sumOf { it.cantidad } }
        .entries.sortedByDescending { it.value }.take(5)

    Column(modifier.fillMaxSize()) {
        ScreenHeader(title = "Estadísticas", subtitle = "Resumen del inventario y la merma")
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile("Productos", "${productos.size}", Icons.Filled.Inventory2, Teal700, Modifier.weight(1f))
                StatTile("Unidades en stock", "$unidades", Icons.Filled.Inventory2, Indigo600, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile("Valor del inventario", formatoMoneda(valorInventario), Icons.Filled.AttachMoney, Teal700, Modifier.weight(1f))
                StatTile("Costo de merma", formatoMoneda(costoMerma), Icons.Filled.DeleteSweep, Red600, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile("Caducan en 7 días", "$porCaducar", Icons.Filled.Schedule, Amber600, Modifier.weight(1f))
                StatTile("Registros de merma", "${mermas.size}", Icons.Filled.DeleteSweep, Orange600, Modifier.weight(1f))
            }

            GraficaCard("Merma por motivo", mermaPorMotivo.map { it.key to it.value }, Red600)
            GraficaCard("Existencias por categoría", stockPorCategoria.map { it.key to it.value }, Teal700)
            GraficaCard("Productos con más merma", topMerma.map { it.key to it.value }, Amber600)
        }
    }
}

@Composable
private fun GraficaCard(titulo: String, datos: List<Pair<String, Int>>, color: Color) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle(titulo)
            if (datos.isEmpty()) {
                Text(
                    "Sin datos todavía",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val maximo = datos.maxOf { it.second }
                datos.forEach { (etiqueta, valor) -> BarRow(etiqueta, valor, maximo, color) }
            }
        }
    }
}
