package mx.edu.utez.proyectointegrador.navigation

sealed class AppRoutes(val route: String) {
    data object Login : AppRoutes("login")
    data object Main : AppRoutes("main")
    data object Usuarios : AppRoutes("usuarios")
    data object ProductoForm : AppRoutes("producto_form/{id}") {
        fun createRoute(id: Long): String = "producto_form/$id"
    }
}
