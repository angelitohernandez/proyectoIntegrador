package mx.edu.utez.proyectointegrador.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mx.edu.utez.proyectointegrador.data.local.Session
import mx.edu.utez.proyectointegrador.data.model.Rol
import mx.edu.utez.proyectointegrador.data.remote.dto.ProductoDto
import mx.edu.utez.proyectointegrador.ui.components.EmptyState
import mx.edu.utez.proyectointegrador.ui.components.ListaPadding
import mx.edu.utez.proyectointegrador.ui.components.MainTab
import mx.edu.utez.proyectointegrador.ui.components.ProductoCard
import mx.edu.utez.proyectointegrador.ui.components.RolChip
import mx.edu.utez.proyectointegrador.ui.components.ScreenHeader
import mx.edu.utez.proyectointegrador.ui.components.SectionTitle
import mx.edu.utez.proyectointegrador.ui.components.StatTile
import mx.edu.utez.proyectointegrador.ui.components.VoiceTextField
import mx.edu.utez.proyectointegrador.ui.theme.Amber600
import mx.edu.utez.proyectointegrador.ui.theme.Indigo600
import mx.edu.utez.proyectointegrador.ui.theme.Orange600
import mx.edu.utez.proyectointegrador.ui.theme.Red600
import mx.edu.utez.proyectointegrador.ui.theme.Teal700
import mx.edu.utez.proyectointegrador.ui.viewmodels.InventarioUiState
import mx.edu.utez.proyectointegrador.util.esCritico
import mx.edu.utez.proyectointegrador.util.stockBajo
import java.time.LocalDate

@Composable
fun HomeScreen(
    session: Session,
    rol: Rol,
    state: InventarioUiState,
    onLogout: () -> Unit,
    onScan: () -> Unit,
    onProductoClick: (ProductoDto) -> Unit,
    onIrA: (MainTab) -> Unit,
    onUsuarios: () -> Unit,
    modifier: Modifier = Modifier
) {
    val productos = state.productos
    val criticos = productos.filter { it.esCritico }
    val promociones = productos.filter { it.descuento > 0 }
    val porSalir = productos.filter { it.caducidad != null }.sortedBy { it.caducidad }
    val bajoStock = productos.filter { it.stockBajo }
    val hoy = LocalDate.now().toString()
    val recibidosHoy = productos.filter { it.creado == hoy }
    val disponibles = productos.filter { it.stock > 0 }
    var busqueda by remember { mutableStateOf("") }

    Column(modifier.fillMaxSize()) {
        ScreenHeader(
            title = "Hola, ${session.nombre.substringBefore(' ')}",
            subtitle = if (state.cargando) "Actualizando…" else "Agita el teléfono para actualizar",
            actions = {
                RolChip(rol)
                IconButton(onClick = onLogout) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión", tint = Color.White)
                }
            }
        )

        LazyColumn(
            contentPadding = ListaPadding,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (rol) {
                Rol.ADMIN -> {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatTile("Críticos", "${criticos.size}", Icons.Filled.Warning, Red600, Modifier.weight(1f))
                            StatTile("Promociones", "${promociones.size}", Icons.Filled.LocalOffer, Orange600, Modifier.weight(1f))
                        }
                    }
                    item { BotonEscanear("Escanear producto", onScan) }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            AccesoRapido("Usuarios", Icons.Filled.Groups, Indigo600, onUsuarios, Modifier.weight(1f))
                            AccesoRapido("Merma", Icons.Filled.DeleteSweep, Amber600, { onIrA(MainTab.MERMA) }, Modifier.weight(1f))
                        }
                    }
                    item { SectionTitle("Prioridad de salida", subtitle = "Lo que caduca primero") }
                    listaProductos(porSalir.take(6), "Sin productos con caducidad", onProductoClick)
                }

                Rol.ENCARGADO -> {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatTile("Recepciones del día", "${recibidosHoy.size}", Icons.Filled.LocalShipping, Teal700, Modifier.weight(1f))
                            StatTile("Stock bajo", "${bajoStock.size}", Icons.Filled.Warning, Red600, Modifier.weight(1f))
                        }
                    }
                    item { BotonEscanear("Escanear producto", onScan) }
                    item { SectionTitle("Órdenes de preparación", subtitle = "Productos por reabastecer") }
                    listaProductos(bajoStock, "Todo el stock está en orden", onProductoClick)
                }

                Rol.VENDEDOR -> {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatTile("Disponibles", "${disponibles.size}", Icons.Filled.CheckCircle, Teal700, Modifier.weight(1f))
                            StatTile("Promociones", "${promociones.size}", Icons.Filled.LocalOffer, Orange600, Modifier.weight(1f))
                        }
                    }
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SectionTitle("Búsqueda y escaneo")
                            VoiceTextField(
                                value = busqueda,
                                onValueChange = { busqueda = it },
                                label = "Buscar por nombre (puedes dictar)",
                                leadingIcon = Icons.Filled.Search,
                                suggestions = productos.map { it.nombre },
                                modifier = Modifier.fillMaxWidth()
                            )
                            BotonEscanear("Escanear código", onScan)
                        }
                    }
                    item { SectionTitle("Catálogo disponible") }
                    val catalogo = disponibles.filter {
                        busqueda.isBlank() || it.nombre.contains(busqueda, ignoreCase = true) ||
                            it.categoria.contains(busqueda, ignoreCase = true)
                    }
                    listaProductos(catalogo, "Sin resultados", onProductoClick)
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.listaProductos(
    lista: List<ProductoDto>,
    vacio: String,
    onClick: (ProductoDto) -> Unit
) {
    if (lista.isEmpty()) {
        item { EmptyState(Icons.Filled.Inventory2, vacio) }
    } else {
        items(lista, key = { it.id }) { producto ->
            ProductoCard(producto, onClick = { onClick(producto) })
        }
    }
}

@Composable
private fun BotonEscanear(texto: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Icon(Icons.Filled.QrCodeScanner, contentDescription = null)
        Spacer(Modifier.size(10.dp))
        Text(texto, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun AccesoRapido(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(52.dp)
    ) {
        Icon(icono, contentDescription = null, tint = color)
        Spacer(Modifier.size(8.dp))
        Text(texto)
    }
}
