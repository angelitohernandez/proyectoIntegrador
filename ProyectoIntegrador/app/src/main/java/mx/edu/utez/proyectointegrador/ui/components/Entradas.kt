package mx.edu.utez.proyectointegrador.ui.components

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import mx.edu.utez.proyectointegrador.util.parseFecha
import java.time.Instant
import java.time.ZoneOffset

@Composable
fun rememberVoiceInput(onResult: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val currentOnResult by rememberUpdatedState(onResult)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
                ?.let { currentOnResult(it) }
        }
    }
    return remember(launcher) {
        {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-MX")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora…")
            }
            try {
                launcher.launch(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "Tu dispositivo no tiene reconocimiento de voz", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun rememberBarcodeScanner(onCode: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val currentOnCode by rememberUpdatedState(onCode)
    return remember(context) {
        {
            val options = GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build()
            GmsBarcodeScanning.getClient(context, options).startScan()
                .addOnSuccessListener { barcode -> barcode.rawValue?.let { currentOnCode(it) } }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "No se pudo abrir el escáner: ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}

@Composable
fun VoiceTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    suggestions: List<String> = emptyList(),
    leadingIcon: ImageVector? = null,
    isError: Boolean = false
) {
    val startVoice = rememberVoiceInput { texto ->
        val limpio = texto.trim()
        onValueChange(
            if (value.isBlank()) limpio.replaceFirstChar { it.uppercase() } else "$value $limpio"
        )
    }
    val leading: (@Composable () -> Unit)? = if (leadingIcon != null) {
        { Icon(leadingIcon, contentDescription = null) }
    } else null

    Column(modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            isError = isError,
            leadingIcon = leading,
            trailingIcon = {
                IconButton(onClick = startVoice) {
                    Icon(
                        Icons.Filled.Mic,
                        contentDescription = "Dictar por voz",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )
        val sugeridas = if (value.isBlank()) {
            emptyList()
        } else {
            suggestions
                .filter { it.contains(value, ignoreCase = true) && !it.equals(value, ignoreCase = true) }
                .distinct()
                .take(5)
        }
        if (sugeridas.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 6.dp)
            ) {
                items(sugeridas) { sugerencia ->
                    SuggestionChip(onClick = { onValueChange(sugerencia) }, label = { Text(sugerencia) })
                }
            }
        }
    }
}

@Composable
fun ClickableField(
    value: String,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = { Icon(icon, contentDescription = null) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable(onClick = onClick)
        )
    }
}

@Composable
fun <T> DropdownField(
    label: String,
    selectedText: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier) {
        ClickableField(
            value = selectedText,
            label = label,
            icon = Icons.Filled.ArrowDropDown,
            onClick = { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(optionLabel(opcion)) },
                    onClick = {
                        onSelected(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaDialog(
    inicial: String?,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit
) {
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = parseFecha(inicial)
            ?.atStartOfDay(ZoneOffset.UTC)
            ?.toInstant()
            ?.toEpochMilli()
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                pickerState.selectedDateMillis?.let { millis ->
                    onSelected(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toString())
                }
                onDismiss()
            }) { Text("Aceptar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    ) {
        DatePicker(state = pickerState)
    }
}
