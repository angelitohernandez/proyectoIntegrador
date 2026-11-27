package mx.edu.utez.proyectointegrador.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mx.edu.utez.proyectointegrador.AppContainer
import mx.edu.utez.proyectointegrador.ui.screens.LoginScreen
import mx.edu.utez.proyectointegrador.ui.screens.MainScreen
import mx.edu.utez.proyectointegrador.ui.screens.ProductoEditarScreen
import mx.edu.utez.proyectointegrador.ui.screens.UsuariosScreen
import mx.edu.utez.proyectointegrador.ui.viewmodels.AuthViewModel
import mx.edu.utez.proyectointegrador.ui.viewmodels.InventarioViewModel

@Composable
fun AppNavGraph(appContainer: AppContainer) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = simpleFactory {
        AuthViewModel(appContainer.authRepository)
    })
    val inventarioViewModel: InventarioViewModel = viewModel(factory = simpleFactory {
        InventarioViewModel(appContainer.inventarioRepository) { authViewModel.logout() }
    })

    val session by authViewModel.sessionState.collectAsStateWithLifecycle()
    val currentSession = session ?: return
    val startDestination = if (currentSession.isLoggedIn && currentSession.rol != null) {
        AppRoutes.Main.route
    } else {
        AppRoutes.Login.route
    }

    LaunchedEffect(currentSession.isLoggedIn) {
        if (!currentSession.isLoggedIn) {
            inventarioViewModel.limpiar()
            val actual = navController.currentDestination?.route
            if (actual != null && actual != AppRoutes.Login.route) {
                navController.navigate(AppRoutes.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppRoutes.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(AppRoutes.Main.route) {
                        popUpTo(AppRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.Main.route) {
            val rol = currentSession.rol
            if (rol != null) {
                MainScreen(
                    session = currentSession,
                    rol = rol,
                    vm = inventarioViewModel,
                    onLogout = { authViewModel.logout() },
                    onEditarProducto = { id -> navController.navigate(AppRoutes.ProductoForm.createRoute(id)) },
                    onUsuarios = { navController.navigate(AppRoutes.Usuarios.route) }
                )
            }
        }

        composable(
            route = AppRoutes.ProductoForm.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: -1L
            val state by inventarioViewModel.state.collectAsStateWithLifecycle()
            ProductoEditarScreen(
                productoId = id,
                state = state,
                vm = inventarioViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.Usuarios.route) {
            val state by inventarioViewModel.state.collectAsStateWithLifecycle()
            UsuariosScreen(
                state = state,
                vm = inventarioViewModel,
                usuarioActualId = currentSession.userId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

private inline fun <reified T : ViewModel> simpleFactory(
    crossinline creator: () -> T
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = creator() as VM
}
