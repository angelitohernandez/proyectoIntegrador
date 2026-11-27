package mx.edu.utez.proyectointegrador.data.remote.dto

data class LoginRequest(val username: String, val password: String)

data class UsuarioDto(
    val id: Long,
    val username: String,
    val nombre: String,
    val rol: String
)

data class LoginResponse(val token: String, val usuario: UsuarioDto)

data class UsuarioRequest(
    val username: String,
    val nombre: String,
    val password: String?,
    val rol: String
)

data class ProductoDto(
    val id: Long,
    val codigo: String,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val stock: Int,
    val stockMinimo: Int,
    val caducidad: String?,
    val descuento: Int,
    val creado: String?,
    val fotoVersion: Long
)

data class ProductoRequest(
    val codigo: String,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val stock: Int,
    val stockMinimo: Int,
    val caducidad: String?,
    val descuento: Int
)

data class MermaDto(
    val id: Long,
    val productoId: Long,
    val productoNombre: String,
    val productoPrecio: Double,
    val cantidad: Int,
    val motivo: String,
    val fecha: String,
    val observaciones: String?,
    val registradoPor: String?
)

data class MermaRequest(
    val productoId: Long,
    val cantidad: Int,
    val motivo: String,
    val fecha: String,
    val observaciones: String?
)

data class ErrorBody(val message: String? = null)
