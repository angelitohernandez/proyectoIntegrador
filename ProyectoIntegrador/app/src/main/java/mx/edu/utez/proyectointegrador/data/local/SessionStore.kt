package mx.edu.utez.proyectointegrador.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import mx.edu.utez.proyectointegrador.data.model.Rol
import mx.edu.utez.proyectointegrador.data.remote.ServerConfig
import mx.edu.utez.proyectointegrador.data.remote.dto.UsuarioDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

data class Session(
    val isLoggedIn: Boolean = false,
    val token: String = "",
    val userId: Long = 0,
    val username: String = "",
    val nombre: String = "",
    val rol: Rol? = null
)

class SessionStore(private val context: Context) {
    private object Keys {
        val TOKEN = stringPreferencesKey("token")
        val USER_ID = longPreferencesKey("user_id")
        val USERNAME = stringPreferencesKey("username")
        val NOMBRE = stringPreferencesKey("nombre")
        val ROL = stringPreferencesKey("rol")
        val LOGGED = booleanPreferencesKey("is_logged_in")
    }

    private val configPrefs = context.getSharedPreferences("server_config", Context.MODE_PRIVATE)

    val sessionFlow: Flow<Session> = context.sessionDataStore.data.map { prefs ->
        Session(
            isLoggedIn = prefs[Keys.LOGGED] ?: false,
            token = prefs[Keys.TOKEN] ?: "",
            userId = prefs[Keys.USER_ID] ?: 0,
            username = prefs[Keys.USERNAME] ?: "",
            nombre = prefs[Keys.NOMBRE] ?: "",
            rol = Rol.from(prefs[Keys.ROL])
        )
    }

    suspend fun save(token: String, usuario: UsuarioDto) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.TOKEN] = token
            prefs[Keys.USER_ID] = usuario.id
            prefs[Keys.USERNAME] = usuario.username
            prefs[Keys.NOMBRE] = usuario.nombre
            prefs[Keys.ROL] = usuario.rol
            prefs[Keys.LOGGED] = true
        }
    }

    suspend fun clear() {
        context.sessionDataStore.edit { it.clear() }
    }

    fun getServerUrl(): String =
        configPrefs.getString("server_url", ServerConfig.DEFAULT_URL) ?: ServerConfig.DEFAULT_URL

    fun setServerUrl(url: String) {
        configPrefs.edit().putString("server_url", url).apply()
    }
}
