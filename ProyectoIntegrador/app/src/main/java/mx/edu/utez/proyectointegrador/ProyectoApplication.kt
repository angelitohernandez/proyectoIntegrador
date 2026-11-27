package mx.edu.utez.proyectointegrador

import android.app.Application

class ProyectoApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
