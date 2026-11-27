package mx.edu.utez.proyectointegrador.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import mx.edu.utez.proyectointegrador.data.remote.dto.ProductoDto
import mx.edu.utez.proyectointegrador.ui.components.EmptyState
import mx.edu.utez.proyectointegrador.ui.components.ListaPadding
import mx.edu.utez.proyectointegrador.ui.components.ProductoCard
import mx.edu.utez.proyectointegrador.ui.components.ProductoImagen
import mx.edu.utez.proyectointegrador.ui.components.ScreenHeader
import mx.edu.utez.proyectointegrador.ui.components.VoiceTextField
import mx.edu.utez.proyectointegrador.ui.viewmodels.InventarioUiState
import mx.edu.utez.proyectointegrador.ui.viewmodels.InventarioViewModel
import mx.edu.utez.proyectointegrador.util.enPromocion
import mx.edu.utez.proyectointegrador.util.formatoFecha
import mx.edu.utez.proyectointegrador.util.formatoMoneda
import mx.edu.utez.proyectointegrador.util.precioFinal

@Composable
fun ProductosScreen(
    state: InventarioUiState,
    vm: InventarioViewModel,
    puedeEditar: Boolean,
    onScan: () -> Unit,
    onProductoClick: (ProductoDto) -> Unit,
    onEditar: (ProductoDto) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var busqueda by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf<String?>(null) }
    var porBorrar by remember { mutableStateOf<ProductoDto?>(null) }

    val categorias = state.productos.map { it.categoria }.distinct().sorted()
    val filtrados = state.productos.filter { p ->
        (categoria == null || p.categoria == categoria) &&
            (busqueda.isBlank() ||
                p.nombre.contains(busqueda, ignoreCase = true) ||
                p.codigo.contains(busqueda, ignoreCase = true) ||
                p.categoria.contains(busqueda, ignoreCase = true))
    }

    Column(modifier.fillMaxSize()) {
        ScreenHeader(
            title = "Productos",
            subtitle = "${state.productos.size} registrados",
            actions = {
                IconButton(onClick = { vm.refresh() }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Actualizar", tint = Color.White)
                }
            }
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VoiceTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = "Buscar producto",
                leadingIcon = Icons.Filled.Search,
                suggestions = state.productos.map { it.nombre },
                modifier = Modifier.weight(1f)
            )
            FilledTonalIconButton(
                onClick = onScan,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(48.dp)
            ) {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = "Escanear")
            }
        }
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = categoria == null,
                    onClick = { categoria = null },
                    label = { Text("Todas") }
                )
            }
            items(categorias) { cat ->
                FilterChip(
                    selected = categoria == cat,
                    onClick = { categoria = if (categoria == cat) null else cat },
                    label = { Text(cat) }
                )
            }
        }
        if (filtrados.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.Inventory2,
                title = "Sin productos",
                subtitle = "Prueba con otra búsqueda o agrega uno nuevo."
            )
        } else {
            LazyColumn(
                contentPadding = ListaPadding,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtrados, key = { it.id }) { producto ->
                    ProductoCard(
                        producto = producto,
                        onClick = { onProductoClick(producto) },
                        trailing = if (puedeEditar) {
                            {
                                Column {
                                    IconButton(onClick = { onEditar(producto) }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { porBorrar = producto }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        } else null
                    )
                }
            }
        }
    }

    porBorrar?.let { producto ->
        AlertDialog(
            onDismissRequest = { porBorrar = null },
            title = { Text("Eliminar producto") },
            text = { Text("¿Eliminar \"${producto.nombre}\"? También se borrarán sus registros de merma.") },
            confirmButton = {
                TextButton(onClick = {
                    porBorrar = null
                    vm.eliminarProducto(producto.id) { resultado ->
                        val mensaje = resultado.fold(
                            onSuccess = { "Producto eliminado" },
                            onFailure = { it.message ?: "No se pudo eliminar" }
                        )
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { porBorrar = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun ProductoDetalleDialog(
    producto: ProductoDto,
    puedeEditar: Boolean,
    onEditar: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(producto.nombre) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProductoImagen(
                    producto,
                    Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                )
                DetalleFila("Código", producto.codigo)
                DetalleFila("Categoría", producto.categoria)
                DetalleFila(
                    "Precio",
                    if (producto.enPromocion) {
                        "${formatoMoneda(producto.precioFinal)} (-${producto.descuento}%)"
                    } else {
                        formatoMoneda(producto.precio)
                    }
                )
                DetalleFila("Stock", "${producto.stock} (mínimo ${producto.stockMinimo})")
                DetalleFila("Caducidad", formatoFecha(producto.caducidad))
            }
        },
        confirmButton = {
            if (puedeEditar) {
                TextButton(onClick = onEditar) { Text("Editar") }
            } else {
                TextButton(onClick = onDismiss) { Text("Cerrar") }
            }
        },
        dismissButton = {
            if (puedeEditar) TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}

@Composable
private fun DetalleFila(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.size(8.dp))
        Text(valor, style = MaterialTheme.typography.bodyMedium)
    }
}
