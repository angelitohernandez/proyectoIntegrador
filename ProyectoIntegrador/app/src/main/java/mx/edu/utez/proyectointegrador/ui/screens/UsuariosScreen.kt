package mx.edu.utez.proyectointegrador.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import mx.edu.utez.proyectointegrador.data.model.Rol
import mx.edu.utez.proyectointegrador.data.remote.dto.UsuarioDto
import mx.edu.utez.proyectointegrador.data.remote.dto.UsuarioRequest
import mx.edu.utez.proyectointegrador.ui.components.DropdownField
import mx.edu.utez.proyectointegrador.ui.components.EmptyState
import mx.edu.utez.proyectointegrador.ui.components.RolChip
import mx.edu.utez.proyectointegrador.ui.components.ScreenHeader
import mx.edu.utez.proyectointegrador.ui.viewmodels.InventarioUiState
import mx.edu.utez.proyectointegrador.ui.viewmodels.InventarioViewModel

@Composable
fun UsuariosScreen(
    state: InventarioUiState,
    vm: InventarioViewModel,
    usuarioActualId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var formVisible by remember { mutableStateOf(false) }
    var enEdicion by remember { mutableStateOf<UsuarioDto?>(null) }
    var porBorrar by remember { mutableStateOf<UsuarioDto?>(null) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            ScreenHeader(
                title = "Usuarios",
                subtitle = "Administra quién usa la app y con qué rol",
                leading = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
            if (state.usuarios.isEmpty()) {
                EmptyState(Icons.Filled.Groups, "Sin usuarios")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.usuarios, key = { it.id }) { usuario ->
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(usuario.nombre, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "@${usuario.username}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Rol.from(usuario.rol)?.let { RolChip(it) }
                                }
                                IconButton(onClick = {
                                    enEdicion = usuario
                                    formVisible = true
                                }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                                }
                                if (usuario.id != usuarioActualId) {
                                    IconButton(onClick = { porBorrar = usuario }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = {
                enEdicion = null
                formVisible = true
            },
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            text = { Text("Nuevo usuario") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }

    if (formVisible) {
        UsuarioFormDialog(
            usuario = enEdicion,
            state = state,
            vm = vm,
            onDismiss = { formVisible = false }
        )
    }

    porBorrar?.let { usuario ->
        AlertDialog(
            onDismissRequest = { porBorrar = null },
            title = { Text("Eliminar usuario") },
            text = { Text("¿Eliminar a \"${usuario.nombre}\"? Ya no podrá iniciar sesión.") },
            confirmButton = {
                TextButton(onClick = {
                    porBorrar = null
                    vm.eliminarUsuario(usuario.id) { resultado ->
                        val mensaje = resultado.fold(
                            onSuccess = { "Usuario eliminado" },
                            onFailure = { it.message ?: "No se pudo eliminar" }
                        )
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { porBorrar = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun UsuarioFormDialog(
    usuario: UsuarioDto?,
    state: InventarioUiState,
    vm: InventarioViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf(usuario?.username ?: "") }
    var nombre by remember { mutableStateOf(usuario?.nombre ?: "") }
    var password by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf(Rol.from(usuario?.rol) ?: Rol.VENDEDOR) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (usuario == null) "Nuevo usuario" else "Editar usuario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it.trim() },
                    label = { Text("Usuario") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(if (usuario == null) "Contraseña" else "Nueva contraseña (opcional)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownField(
                    label = "Rol",
                    selectedText = rol.label,
                    options = Rol.entries.toList(),
                    optionLabel = { it.label },
                    onSelected = { rol = it }
                )
                if (error != null) {
                    Text(error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !state.guardando,
                onClick = {
                    when {
                        username.isBlank() || nombre.isBlank() -> error = "Completa usuario y nombre"
                        usuario == null && password.length < 4 -> error = "La contraseña debe tener al menos 4 caracteres"
                        password.isNotEmpty() && password.length < 4 -> error = "La contraseña debe tener al menos 4 caracteres"
                        else -> {
                            val request = UsuarioRequest(
                                username = username,
                                nombre = nombre.trim(),
                                password = password.ifBlank { null },
                                rol = rol.name
                            )
                            vm.guardarUsuario(usuario?.id, request) { resultado ->
                                resultado
                                    .onSuccess {
                                        Toast.makeText(context, "Usuario guardado", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    }
                                    .onFailure { error = it.message ?: "No se pudo guardar" }
                            }
                        }
                    }
                }
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
