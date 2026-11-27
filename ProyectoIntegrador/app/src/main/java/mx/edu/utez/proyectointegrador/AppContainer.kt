package mx.edu.utez.proyectointegrador

import android.content.Context
import mx.edu.utez.proyectointegrador.data.local.SessionStore
import mx.edu.utez.proyectointegrador.data.remote.ApiService
import mx.edu.utez.proyectointegrador.data.remote.RetrofitClient
import mx.edu.utez.proyectointegrador.data.remote.ServerConfig
import mx.edu.utez.proyectointegrador.data.repository.AuthRepository
import mx.edu.utez.proyectointegrador.data.repository.InventarioRepository

class AppContainer(context: Context) {
    val sessionStore = SessionStore(context)

    init {
        ServerConfig.baseUrl = sessionStore.getServerUrl()
    }

    val apiService: ApiService = RetrofitClient.apiService

    val authRepository = AuthRepository(
        api = apiService,
        store = sessionStore
    )

    val inventarioRepository = InventarioRepository(
        context = context.applicationContext,
        api = apiService
    )
}
