package mx.edu.utez.proyectointegrador.data.repository

import mx.edu.utez.proyectointegrador.data.local.Session
import mx.edu.utez.proyectointegrador.data.local.SessionStore
import mx.edu.utez.proyectointegrador.data.remote.ApiService
import mx.edu.utez.proyectointegrador.data.remote.ServerConfig
import mx.edu.utez.proyectointegrador.data.remote.dto.LoginRequest
import mx.edu.utez.proyectointegrador.data.remote.safeCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class AuthRepository(
    private val api: ApiService,
    private val store: SessionStore
) {
    val sessionFlow: Flow<Session> = store.sessionFlow.onEach {
        ServerConfig.token = it.token.ifBlank { null }
    }

    suspend fun login(username: String, password: String): Result<Unit> {
        val result = safeCall { api.login(LoginRequest(username.trim(), password)) }
        return result.map { respuesta ->
            ServerConfig.token = respuesta.token
            store.save(respuesta.token, respuesta.usuario)
        }
    }

    suspend fun logout() {
        ServerConfig.token = null
        store.clear()
    }

    fun getServerUrl(): String = store.getServerUrl()

    fun setServerUrl(raw: String): String {
        val url = ServerConfig.normalizar(raw)
        ServerConfig.baseUrl = url
        store.setServerUrl(url)
        return url
    }
}
