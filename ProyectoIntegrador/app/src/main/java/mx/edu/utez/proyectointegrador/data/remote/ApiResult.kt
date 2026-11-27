package mx.edu.utez.proyectointegrador.data.remote

import mx.edu.utez.proyectointegrador.data.remote.dto.ErrorBody
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import retrofit2.Response
import java.io.IOException

class ApiException(val code: Int, message: String) : Exception(message)

private val gson = Gson()

private fun mensajePorCodigo(code: Int): String = when (code) {
    401 -> "Tu sesión expiró. Inicia sesión de nuevo."
    403 -> "No tienes permiso para realizar esta acción."
    404 -> "No se encontró el recurso solicitado."
    409 -> "Ya existe un registro con esos datos."
    else -> "Error del servidor ($code)."
}

@Suppress("UNCHECKED_CAST")
suspend fun <T> safeCall(block: suspend () -> Response<T>): Result<T> {
    return try {
        val response = block()
        if (response.isSuccessful) {
            Result.success(response.body() ?: (Unit as T))
        } else {
            val mensaje = runCatching {
                gson.fromJson(response.errorBody()?.string(), ErrorBody::class.java)?.message
            }.getOrNull()
            Result.failure(ApiException(response.code(), mensaje?.takeIf { it.isNotBlank() } ?: mensajePorCodigo(response.code())))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: IOException) {
        Result.failure(
            ApiException(0, "No se pudo conectar con el servidor. Revisa la dirección del servidor y que el backend esté encendido.")
        )
    } catch (e: Exception) {
        Result.failure(ApiException(-1, e.message ?: "Ocurrió un error inesperado."))
    }
}
