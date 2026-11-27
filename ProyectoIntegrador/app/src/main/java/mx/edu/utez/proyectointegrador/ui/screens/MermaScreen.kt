package mx.edu.utez.proyectointegrador.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import mx.edu.utez.proyectointegrador.data.remote.dto.MermaDto
import mx.edu.utez.proyectointegrador.data.remote.dto.MermaRequest
import mx.edu.utez.proyectointegrador.ui.components.ClickableField
import mx.edu.utez.proyectointegrador.ui.components.DropdownField
import mx.edu.utez.proyectointegrador.ui.components.EmptyState
import mx.edu.utez.proyectointegrador.ui.components.FechaDialog
import mx.edu.utez.proyectointegrador.ui.components.Pill
import mx.edu.utez.proyectointegrador.ui.components.ScreenHeader
import mx.edu.utez.proyectointegrador.ui.components.StatTile
import mx.edu.utez.proyectointegrador.ui.components.VoiceTextField
import mx.edu.utez.proyectointegrador.ui.theme.Amber600
import mx.edu.utez.proyectointegrador.ui.theme.Red600
import mx.edu.utez.proyectointegrador.ui.viewmodels.InventarioUiState
import mx.edu.utez.proyectointegrador.ui.viewmodels.InventarioViewModel
import mx.edu.utez.proyectointegrador.util.formatoFecha
import mx.edu.utez.proyectointegrador.util.formatoMoneda
import java.time.LocalDate

private val motivos = listOf("Caducado", "Dañado", "Robo", "Devolución", "Otro")

@Composable
fun MermaScreen(
    state: InventarioUiState,
    vm: InventarioViewModel,
    puedeEditar: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var formVisible by remember { mutableStateOf(false) }
    var enEdicion by remember { mutableStateOf<MermaDto?>(null) }
    var porBorrar by remember { mutableStateOf<MermaDto?>(null) }

    val unidades = state.mermas.sumOf { it.cantidad }
    val costo = state.mermas.sumOf { it.cantidad * it.productoPrecio }

    Box(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(
                title = "Merma",
                subtitle = if (puedeEditar) "Registra y administra las pérdidas" else "Consulta de pérdidas registradas",
                actions = {
                    IconButton(onClick = { vm.refresh() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar", tint = Color.White)
                    }
                }
            )
            LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatTile("Unidades perdidas", "$unidades", Icons.Filled.DeleteSweep, Amber600, Modifier.weight(1f))
                        StatTile("Costo estimado", formatoMoneda(costo), Icons.Filled.DeleteSweep, Red600, Modifier.weight(1f))
                    }
                }
                if (state.mermas.isEmpty()) {
                    item { EmptyState(Icons.Filled.DeleteSweep, "Sin merma registrada", subtitle = "Todo en orden por ahora.") }
                } else {
                    items(state.mermas, key = { it.id }) { merma ->
                        MermaCard(
                            merma = merma,
                            puedeEditar = puedeEditar,
                            onEditar = {
                                enEdicion = merma
                                formVisible = true
                            },
                            onBorrar = { porBorrar = merma }
                        )
                    }
                }
            }
        }
        if (puedeEditar) {
            ExtendedFloatingActionButton(
                onClick = {
                    enEdicion = null
                    formVisible = true
                },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Registrar merma") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }

    if (formVisible) {
        MermaFormDialog(
            merma = enEdicion,
            state = state,
            vm = vm,
            onDismiss = { formVisible = false }
        )
    }

    porBorrar?.let { merma ->
        AlertDialog(
            onDismissRequest = { porBorrar = null },
            title = { Text("Eliminar registro") },
            text = { Text("Se eliminará la merma de \"${merma.productoNombre}\" y se devolverán ${merma.cantidad} unidades al stock.") },
            confirmButton = {
                TextButton(onClick = {
                    porBorrar = null
                    vm.eliminarMerma(merma.id) { resultado ->
                        val mensaje = resultado.fold(
                            onSuccess = { "Registro eliminado" },
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
private fun MermaCard(
    merma: MermaDto,
    puedeEditar: Boolean,
    onEditar: () -> Unit,
    onBorrar: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Amber600.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.DeleteSweep, contentDescription = null, tint = Amber600)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(merma.productoNombre, style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Pill("${merma.cantidad} uds", Red600.copy(alpha = 0.12f), Red600)
                    Pill(merma.motivo, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    formatoFecha(merma.fecha) + (merma.registradoPor?.let { " · $it" } ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (!merma.observaciones.isNullOrBlank()) {
                    Text(merma.observaciones, style = MaterialTheme.typography.bodySmall)
                }
            }
            if (puedeEditar) {
                Column {
                    IconButton(onClick = onEditar) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onBorrar) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun MermaFormDialog(
    merma: MermaDto?,
    state: InventarioUiState,
    vm: InventarioViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var productoId by remember { mutableStateOf(merma?.productoId) }
    var cantidad by remember { mutableStateOf(merma?.cantidad?.toString() ?: "") }
    var motivo by remember { mutableStateOf(merma?.motivo ?: motivos.first()) }
    var fecha by remember { mutableStateOf(merma?.fecha ?: LocalDate.now().toString()) }
    var observaciones by remember { mutableStateOf(merma?.observaciones ?: "") }
    var error by remember { mutableStateOf<String?>(null) }
    var mostrarFecha by remember { mutableStateOf(false) }

    val productoSeleccionado = state.productos.firstOrNull { it.id == productoId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (merma == null) "Registrar merma" else "Editar merma") },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DropdownField(
                    label = "Producto",
                    selectedText = productoSeleccionado?.nombre ?: merma?.productoNombre ?: "",
                    options = state.productos,
                    optionLabel = { "${it.nombre} (stock ${it.stock})" },
                    onSelected = { productoId = it.id }
                )
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it.filter { c -> c.isDigit() } },
                    label = { Text("Cantidad") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownField(
                    label = "Motivo",
                    selectedText = motivo,
                    options = motivos,
                    optionLabel = { it },
                    onSelected = { motivo = it }
                )
                ClickableField(
                    value = formatoFecha(fecha),
                    label = "Fecha",
                    icon = Icons.Filled.CalendarMonth,
                    onClick = { mostrarFecha = true }
                )
                VoiceTextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    label = "Observaciones (opcional)",
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Text(error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !state.guardando,
                onClick = {
                    val cantidadNum = cantidad.toIntOrNull()
                    val idProducto = productoId
                    when {
                        idProducto == null -> error = "Selecciona un producto"
                        cantidadNum == null || cantidadNum <= 0 -> error = "Indica una cantidad mayor a 0"
                        else -> {
                            val request = MermaRequest(
                                productoId = idProducto,
                                cantidad = cantidadNum,
                                motivo = motivo,
                                fecha = fecha,
                                observaciones = observaciones.trim().ifBlank { null }
                            )
                            vm.guardarMerma(merma?.id, request) { resultado ->
                                resultado
                                    .onSuccess {
                                        Toast.makeText(context, "Merma guardada", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    }
                                    .onFailure { error = it.message ?: "No se pudo guardar" }
                            }
                        }
                    }
                }
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )

    if (mostrarFecha) {
        FechaDialog(
            inicial = fecha,
            onDismiss = { mostrarFecha = false },
            onSelected = { fecha = it }
        )
    }
}
