package mx.edu.utez.proyectointegrador.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import mx.edu.utez.proyectointegrador.data.remote.RetrofitClient
import mx.edu.utez.proyectointegrador.data.remote.dto.ProductoDto
import mx.edu.utez.proyectointegrador.data.remote.dto.ProductoRequest
import mx.edu.utez.proyectointegrador.ui.components.ClickableField
import mx.edu.utez.proyectointegrador.ui.components.FechaDialog
import mx.edu.utez.proyectointegrador.ui.components.Pill
import mx.edu.utez.proyectointegrador.ui.components.ScreenHeader
import mx.edu.utez.proyectointegrador.ui.components.VoiceTextField
import mx.edu.utez.proyectointegrador.ui.components.rememberBarcodeScanner
import mx.edu.utez.proyectointegrador.ui.viewmodels.InventarioUiState
import mx.edu.utez.proyectointegrador.ui.viewmodels.InventarioViewModel
import mx.edu.utez.proyectointegrador.util.formatoFecha
import kotlinx.coroutines.launch
import java.io.File

private val categoriasBase = listOf(
    "Lácteos", "Frutas y verduras", "Carnes", "Panadería", "Bebidas",
    "Abarrotes", "Limpieza", "Congelados", "Botanas"
)

@Composable
fun ProductoFormContent(
    producto: ProductoDto?,
    codigoInicial: String?,
    state: InventarioUiState,
    vm: InventarioViewModel,
    onGuardado: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val clave = producto?.id ?: -1L

    var codigo by rememberSaveable(clave, codigoInicial) { mutableStateOf(producto?.codigo ?: codigoInicial ?: "") }
    var nombre by rememberSaveable(clave) { mutableStateOf(producto?.nombre ?: "") }
    var categoria by rememberSaveable(clave) { mutableStateOf(producto?.categoria ?: "") }
    var precio by rememberSaveable(clave) {
        mutableStateOf(
            producto?.precio?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: ""
        )
    }
    var descuento by rememberSaveable(clave) { mutableStateOf(producto?.descuento?.toString() ?: "0") }
    var stock by rememberSaveable(clave) { mutableStateOf(producto?.stock?.toString() ?: "") }
    var stockMinimo by rememberSaveable(clave) { mutableStateOf(producto?.stockMinimo?.toString() ?: "5") }
    var caducidad by rememberSaveable(clave) { mutableStateOf(producto?.caducidad ?: "") }
    var fotoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var pendienteUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var mostrarFecha by remember { mutableStateOf(false) }

    val tomarFoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        if (exito) fotoUri = pendienteUri
    }
    val escanear = rememberBarcodeScanner { codigo = it }

    fun abrirCamara() {
        val archivo = File(context.cacheDir, "foto_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
        pendienteUri = uri
        tomarFoto.launch(uri)
    }

    fun mostrar(mensaje: String) {
        scope.launch { snackbar.showSnackbar(mensaje) }
    }

    fun guardar() {
        val precioNum = precio.toDoubleOrNull()
        val stockNum = stock.toIntOrNull()
        val minimoNum = stockMinimo.toIntOrNull()
        val descuentoNum = descuento.toIntOrNull()
        when {
            codigo.isBlank() -> mostrar("Escribe o escanea el código del producto")
            nombre.isBlank() -> mostrar("Escribe el nombre del producto")
            categoria.isBlank() -> mostrar("Indica la categoría")
            precioNum == null || precioNum < 0 -> mostrar("Precio no válido")
            stockNum == null || stockNum < 0 -> mostrar("Stock no válido")
            minimoNum == null || minimoNum < 0 -> mostrar("Stock mínimo no válido")
            descuentoNum == null || descuentoNum !in 0..100 -> mostrar("El descuento debe estar entre 0 y 100")
            else -> {
                val request = ProductoRequest(
                    codigo = codigo.trim(),
                    nombre = nombre.trim(),
                    categoria = categoria.trim(),
                    precio = precioNum,
                    stock = stockNum,
                    stockMinimo = minimoNum,
                    caducidad = caducidad.ifBlank { null },
                    descuento = descuentoNum
                )
                vm.guardarProducto(producto?.id, request, fotoUri) { resultado ->
                    resultado
                        .onSuccess {
                            Toast.makeText(context, "Producto guardado", Toast.LENGTH_SHORT).show()
                            onGuardado()
                        }
                        .onFailure { mostrar(it.message ?: "No se pudo guardar") }
                }
            }
        }
    }

    Column(modifier.fillMaxSize()) {
        ScreenHeader(
            title = if (producto == null) "Nuevo producto" else "Editar producto",
            subtitle = "Usa la cámara y el micrófono para capturar más rápido",
            leading = if (onBack != null) {
                {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            } else null
        )
        Box(Modifier.weight(1f)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { abrirCamara() },
                    contentAlignment = Alignment.Center
                ) {
                    val tieneFotoRemota = producto != null && producto.fotoVersion > 0
                    when {
                        fotoUri != null -> AsyncImage(
                            model = fotoUri,
                            contentDescription = "Foto del producto",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        tieneFotoRemota -> AsyncImage(
                            model = RetrofitClient.fotoUrl(producto!!),
                            contentDescription = "Foto del producto",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.AddAPhoto,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(42.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Toca para tomar una foto",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    if (fotoUri != null || tieneFotoRemota) {
                        Pill(
                            "Cambiar foto",
                            Color.Black.copy(alpha = 0.55f),
                            Color.White,
                            Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = codigo,
                        onValueChange = { codigo = it },
                        label = { Text("Código de barras") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    )
                    FilledTonalIconButton(onClick = escanear, modifier = Modifier.size(52.dp)) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = "Escanear código")
                    }
                }

                VoiceTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = "Nombre del producto",
                    suggestions = state.productos.map { it.nombre },
                    modifier = Modifier.fillMaxWidth()
                )
                VoiceTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = "Categoría",
                    suggestions = state.productos.map { it.categoria } + categoriasBase,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio") },
                        prefix = { Text("$") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = descuento,
                        onValueChange = { descuento = it.filter { c -> c.isDigit() }.take(3) },
                        label = { Text("Promoción %") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it.filter { c -> c.isDigit() } },
                        label = { Text("Stock") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = stockMinimo,
                        onValueChange = { stockMinimo = it.filter { c -> c.isDigit() } },
                        label = { Text("Stock mínimo") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ClickableField(
                        value = if (caducidad.isBlank()) "" else formatoFecha(caducidad),
                        label = "Fecha de caducidad (opcional)",
                        icon = Icons.Filled.CalendarMonth,
                        onClick = { mostrarFecha = true },
                        modifier = Modifier.weight(1f)
                    )
                    if (caducidad.isNotBlank()) {
                        IconButton(onClick = { caducidad = "" }) {
                            Icon(Icons.Filled.Close, contentDescription = "Quitar fecha")
                        }
                    }
                }

                Button(
                    onClick = { guardar() },
                    enabled = !state.guardando,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    if (state.guardando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (producto == null) "Guardar producto" else "Guardar cambios")
                    }
                }
                Spacer(Modifier.height(80.dp))
            }
            SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
        }
    }

    if (mostrarFecha) {
        FechaDialog(
            inicial = caducidad.ifBlank { null },
            onDismiss = { mostrarFecha = false },
            onSelected = { caducidad = it }
        )
    }
}

@Composable
fun ProductoEditarScreen(
    productoId: Long,
    state: InventarioUiState,
    vm: InventarioViewModel,
    onBack: () -> Unit
) {
    val producto = state.productos.firstOrNull { it.id == productoId }
    if (producto == null) {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(
                title = "Editar producto",
                leading = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
            Text(
                "No se encontró el producto. Regresa y actualiza la lista.",
                modifier = Modifier.padding(24.dp)
            )
            TextButton(onClick = onBack, modifier = Modifier.padding(horizontal = 16.dp)) { Text("Volver") }
        }
    } else {
        ProductoFormContent(
            producto = producto,
            codigoInicial = null,
            state = state,
            vm = vm,
            onGuardado = onBack,
            onBack = onBack
        )
    }
}
