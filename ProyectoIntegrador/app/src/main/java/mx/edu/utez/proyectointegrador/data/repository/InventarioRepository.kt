package mx.edu.utez.proyectointegrador.data.repository

import android.content.Context
import android.net.Uri
import mx.edu.utez.proyectointegrador.data.remote.ApiService
import mx.edu.utez.proyectointegrador.data.remote.dto.*
import mx.edu.utez.proyectointegrador.data.remote.safeCall
import mx.edu.utez.proyectointegrador.util.ImageUtils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class InventarioRepository(
    private val context: Context,
    private val api: ApiService
) {
    private val gson = Gson()

    suspend fun productos() = safeCall { api.productos() }

    suspend fun mermas() = safeCall { api.mermas() }

    suspend fun usuarios() = safeCall { api.usuarios() }

    suspend fun guardarProducto(id: Long?, request: ProductoRequest, fotoUri: Uri?): Result<ProductoDto> {
        val json = gson.toJson(request).toRequestBody("application/json".toMediaType())
        val parteFoto = fotoUri?.let { uri ->
            withContext(Dispatchers.IO) { ImageUtils.comprimir(context, uri) }
        }?.let { bytes ->
            MultipartBody.Part.createFormData(
                "foto",
                "foto.jpg",
                bytes.toRequestBody("image/jpeg".toMediaType())
            )
        }
        return safeCall {
            if (id == null) api.crearProducto(json, parteFoto) else api.actualizarProducto(id, json, parteFoto)
        }
    }

    suspend fun eliminarProducto(id: Long) = safeCall { api.eliminarProducto(id) }

    suspend fun guardarMerma(id: Long?, request: MermaRequest) = safeCall {
        if (id == null) api.crearMerma(request) else api.actualizarMerma(id, request)
    }

    suspend fun eliminarMerma(id: Long) = safeCall { api.eliminarMerma(id) }

    suspend fun guardarUsuario(id: Long?, request: UsuarioRequest) = safeCall {
        if (id == null) api.crearUsuario(request) else api.actualizarUsuario(id, request)
    }

    suspend fun eliminarUsuario(id: Long) = safeCall { api.eliminarUsuario(id) }
}
