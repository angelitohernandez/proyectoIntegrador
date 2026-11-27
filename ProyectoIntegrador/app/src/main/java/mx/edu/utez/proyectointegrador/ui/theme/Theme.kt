package mx.edu.utez.proyectointegrador.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Teal700,
    onPrimary = Color.White,
    primaryContainer = Teal100,
    onPrimaryContainer = Teal900,
    secondary = Orange600,
    onSecondary = Color.White,
    secondaryContainer = Orange100,
    onSecondaryContainer = Color(0xFF7C2D12),
    tertiary = Indigo600,
    onTertiary = Color.White,
    tertiaryContainer = Indigo100,
    onTertiaryContainer = Color(0xFF312E81),
    background = Mint50,
    onBackground = Color(0xFF0F1F1D),
    surface = Color.White,
    onSurface = Color(0xFF0F1F1D),
    surfaceVariant = Color(0xFFE3EEEC),
    onSurfaceVariant = Color(0xFF475B58),
    error = Red600,
    errorContainer = Red100
)

private val DarkColors = darkColorScheme(
    primary = Teal300,
    onPrimary = Teal900,
    primaryContainer = Color(0xFF115E59),
    onPrimaryContainer = Teal100,
    secondary = Color(0xFFFDBA74),
    onSecondary = Color(0xFF431407),
    secondaryContainer = Color(0xFF7C2D12),
    onSecondaryContainer = Orange100,
    tertiary = Color(0xFFA5B4FC),
    onTertiary = Color(0xFF1E1B4B),
    tertiaryContainer = Color(0xFF3730A3),
    onTertiaryContainer = Indigo100,
    background = Color(0xFF0B1514),
    onBackground = Color(0xFFE2F0EE),
    surface = Color(0xFF13211F),
    onSurface = Color(0xFFE2F0EE),
    surfaceVariant = Color(0xFF243735),
    onSurfaceVariant = Color(0xFFABC2BE),
    error = Color(0xFFFCA5A5),
    errorContainer = Color(0xFF7F1D1D)
)

@Composable
fun ProyectoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
