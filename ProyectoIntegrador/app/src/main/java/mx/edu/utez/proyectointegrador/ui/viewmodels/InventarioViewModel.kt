package mx.edu.utez.proyectointegrador.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import mx.edu.utez.proyectointegrador.data.model.Rol
import mx.edu.utez.proyectointegrador.data.remote.ApiException
import mx.edu.utez.proyectointegrador.data.remote.dto.*
import mx.edu.utez.proyectointegrador.data.repository.InventarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InventarioUiState(
    val productos: List<ProductoDto> = emptyList(),
    val mermas: List<MermaDto> = emptyList(),
    val usuarios: List<UsuarioDto> = emptyList(),
    val cargando: Boolean = false,
    val guardando: Boolean = false,
    val error: String? = null
)

class InventarioViewModel(
    private val repository: InventarioRepository,
    private val onUnauthorized: () -> Unit
) : ViewModel() {
    private val _state = MutableStateFlow(InventarioUiState())
    val state: StateFlow<InventarioUiState> = _state.asStateFlow()

    private var rol: Rol? = null

    fun iniciar(nuevoRol: Rol) {
        rol = nuevoRol
        refresh()
    }

    fun limpiar() {
        rol = null
        _state.value = InventarioUiState()
    }

    fun consumirError() {
        _state.update { it.copy(error = null) }
    }

    fun refresh() {
        if (rol == null) return
        viewModelScope.launch { cargarTodo() }
    }

    private fun revisarSesion(error: Throwable) {
        if (error is ApiException && error.code == 401) onUnauthorized()
    }

    private fun reportar(error: Throwable) {
        revisarSesion(error)
        _state.update { it.copy(error = error.message) }
    }

    private suspend fun cargarTodo() {
        _state.update { it.copy(cargando = true) }
        repository.productos()
            .onSuccess { lista -> _state.update { it.copy(productos = lista) } }
            .onFailure(::reportar)
        repository.mermas()
            .onSuccess { lista -> _state.update { it.copy(mermas = lista) } }
            .onFailure { revisarSesion(it) }
        if (rol == Rol.ADMIN) {
            repository.usuarios()
                .onSuccess { lista -> _state.update { it.copy(usuarios = lista) } }
                .onFailure { revisarSesion(it) }
        }
        _state.update { it.copy(cargando = false) }
    }

    private fun <T> accion(onResult: (Result<Unit>) -> Unit, llamada: suspend () -> Result<T>) {
        viewModelScope.launch {
            _state.update { it.copy(guardando = true) }
            val resultado = llamada()
            resultado.onFailure { revisarSesion(it) }
            if (resultado.isSuccess) cargarTodo()
            _state.update { it.copy(guardando = false) }
            onResult(resultado.map { })
        }
    }

    fun guardarProducto(id: Long?, request: ProductoRequest, foto: Uri?, onResult: (Result<Unit>) -> Unit) =
        accion(onResult) { repository.guardarProducto(id, request, foto) }

    fun eliminarProducto(id: Long, onResult: (Result<Unit>) -> Unit) =
        accion(onResult) { repository.eliminarProducto(id) }

    fun guardarMerma(id: Long?, request: MermaRequest, onResult: (Result<Unit>) -> Unit) =
        accion(onResult) { repository.guardarMerma(id, request) }

    fun eliminarMerma(id: Long, onResult: (Result<Unit>) -> Unit) =
        accion(onResult) { repository.eliminarMerma(id) }

    fun guardarUsuario(id: Long?, request: UsuarioRequest, onResult: (Result<Unit>) -> Unit) =
        accion(onResult) { repository.guardarUsuario(id, request) }

    fun eliminarUsuario(id: Long, onResult: (Result<Unit>) -> Unit) =
        accion(onResult) { repository.eliminarUsuario(id) }
}
