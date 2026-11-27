package mx.edu.utez.proyectointegrador.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.proyectointegrador.data.local.Session
import mx.edu.utez.proyectointegrador.data.model.Rol
import mx.edu.utez.proyectointegrador.data.remote.dto.ProductoDto
import mx.edu.utez.proyectointegrador.ui.components.BottomNavBar
import mx.edu.utez.proyectointegrador.ui.components.MainTab
import mx.edu.utez.proyectointegrador.ui.components.ShakeDetector
import mx.edu.utez.proyectointegrador.ui.components.rememberBarcodeScanner
import mx.edu.utez.proyectointegrador.ui.components.tabsPara
import mx.edu.utez.proyectointegrador.ui.viewmodels.InventarioViewModel

@Composable
fun MainScreen(
    session: Session,
    rol: Rol,
    vm: InventarioViewModel,
    onLogout: () -> Unit,
    onEditarProducto: (Long) -> Unit,
    onUsuarios: () -> Unit
) {
    val context = LocalContext.current
    val state by vm.state.collectAsStateWithLifecycle()
    val tabs = tabsPara(rol)
    var tab by rememberSaveable { mutableStateOf(MainTab.INICIO) }
    var codigoPendiente by rememberSaveable { mutableStateOf<String?>(null) }
    var detalle by remember { mutableStateOf<ProductoDto?>(null) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(rol) { vm.iniciar(rol) }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbar.showSnackbar(it)
            vm.consumirError()
        }
    }

    ShakeDetector {
        vm.refresh()
        Toast.makeText(context, "Actualizando datos…", Toast.LENGTH_SHORT).show()
    }

    val escanear = rememberBarcodeScanner { codigo ->
        val encontrado = state.productos.firstOrNull { it.codigo == codigo }
        when {
            encontrado != null -> detalle = encontrado
            rol != Rol.VENDEDOR -> {
                codigoPendiente = codigo
                tab = MainTab.AGREGAR
                Toast.makeText(context, "Código nuevo: completa los datos del producto", Toast.LENGTH_LONG).show()
            }
            else -> Toast.makeText(context, "Producto no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    val tabActual = if (tab in tabs) tab else MainTab.INICIO

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = { BottomNavBar(tabs, tabActual) { tab = it } }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            when (tabActual) {
                MainTab.INICIO -> HomeScreen(
                    session = session,
                    rol = rol,
                    state = state,
                    onLogout = onLogout,
                    onScan = escanear,
                    onProductoClick = { detalle = it },
                    onIrA = { tab = it },
                    onUsuarios = onUsuarios
                )

                MainTab.AGREGAR -> ProductoFormContent(
                    producto = null,
                    codigoInicial = codigoPendiente,
                    state = state,
                    vm = vm,
                    onGuardado = {
                        codigoPendiente = null
                        tab = if (rol == Rol.ENCARGADO) MainTab.INICIO else MainTab.PRODUCTOS
                    }
                )

                MainTab.PRODUCTOS -> ProductosScreen(
                    state = state,
                    vm = vm,
                    puedeEditar = rol == Rol.ADMIN,
                    onScan = escanear,
                    onProductoClick = { detalle = it },
                    onEditar = { onEditarProducto(it.id) }
                )

                MainTab.MERMA -> MermaScreen(
                    state = state,
                    vm = vm,
                    puedeEditar = rol == Rol.ADMIN
                )

                MainTab.ESTADISTICAS -> EstadisticasScreen(state = state)
            }
        }
    }

    detalle?.let { producto ->
        ProductoDetalleDialog(
            producto = producto,
            puedeEditar = rol == Rol.ADMIN,
            onEditar = {
                detalle = null
                onEditarProducto(producto.id)
            },
            onDismiss = { detalle = null }
        )
    }
}
