package mx.edu.utez.proyectointegrador.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mx.edu.utez.proyectointegrador.data.model.Rol
import mx.edu.utez.proyectointegrador.data.remote.RetrofitClient
import mx.edu.utez.proyectointegrador.data.remote.dto.ProductoDto
import mx.edu.utez.proyectointegrador.ui.theme.Amber100
import mx.edu.utez.proyectointegrador.ui.theme.Amber600
import mx.edu.utez.proyectointegrador.ui.theme.GradientEnd
import mx.edu.utez.proyectointegrador.ui.theme.GradientStart
import mx.edu.utez.proyectointegrador.ui.theme.Green100
import mx.edu.utez.proyectointegrador.ui.theme.Green600
import mx.edu.utez.proyectointegrador.ui.theme.Indigo600
import mx.edu.utez.proyectointegrador.ui.theme.Orange600
import mx.edu.utez.proyectointegrador.ui.theme.Red100
import mx.edu.utez.proyectointegrador.ui.theme.Red600
import mx.edu.utez.proyectointegrador.ui.theme.Teal700
import mx.edu.utez.proyectointegrador.util.diasParaCaducar
import mx.edu.utez.proyectointegrador.util.enPromocion
import mx.edu.utez.proyectointegrador.util.formatoFecha
import mx.edu.utez.proyectointegrador.util.formatoMoneda
import mx.edu.utez.proyectointegrador.util.precioFinal

@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(Brush.linearGradient(listOf(GradientStart, GradientEnd)))
    ) {
        Row(
            Modifier
                .statusBarsPadding()
                .padding(start = if (leading != null) 4.dp else 20.dp, end = 8.dp, top = 14.dp, bottom = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leading != null) leading()
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                if (subtitle != null) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
            actions()
        }
    }
}

@Composable
fun StatTile(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(Modifier.height(10.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium)
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier, subtitle: String? = null) {
    Column(modifier.padding(top = 4.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (subtitle != null) {
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun Pill(text: String, background: Color, foreground: Color, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(50), color = background, modifier = modifier) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = foreground,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun RolChip(rol: Rol, modifier: Modifier = Modifier) {
    val (bg, fg) = when (rol) {
        Rol.ADMIN -> Indigo600.copy(alpha = 0.15f) to Indigo600
        Rol.ENCARGADO -> Teal700.copy(alpha = 0.15f) to Teal700
        Rol.VENDEDOR -> Orange600.copy(alpha = 0.15f) to Orange600
    }
    Pill(rol.label, bg, fg, modifier)
}

@Composable
fun ProductoImagen(producto: ProductoDto, modifier: Modifier = Modifier) {
    if (producto.fotoVersion > 0) {
        AsyncImage(
            model = RetrofitClient.fotoUrl(producto),
            contentDescription = producto.nombre,
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(RoundedCornerShape(14.dp))
        )
    } else {
        Box(
            modifier
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Inventory2,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ProductoCard(
    producto: ProductoDto,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ProductoImagen(producto, Modifier.size(68.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${producto.categoria} · ${producto.codigo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (producto.enPromocion) {
                        Text(
                            formatoMoneda(producto.precio),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            formatoMoneda(producto.precioFinal),
                            style = MaterialTheme.typography.titleMedium,
                            color = Orange600
                        )
                        Spacer(Modifier.width(6.dp))
                        Pill("-${producto.descuento}%", Orange600, Color.White)
                    } else {
                        Text(
                            formatoMoneda(producto.precio),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)) {
                    val bajo = producto.stock <= producto.stockMinimo
                    Pill(
                        "Stock: ${producto.stock}",
                        if (bajo) Red100 else Green100,
                        if (bajo) Red600 else Green600
                    )
                    CaducidadPill(producto.caducidad)
                }
            }
            if (trailing != null) trailing()
        }
    }
}

@Composable
fun CaducidadPill(caducidad: String?) {
    val dias = diasParaCaducar(caducidad) ?: return
    when {
        dias < 0 -> Pill("Vencido", Red100, Red600)
        dias <= 3 -> Pill("Caduca en ${dias}d", Red100, Red600)
        dias <= 7 -> Pill("Caduca en ${dias}d", Amber100, Amber600)
        else -> Pill("Cad. ${formatoFecha(caducidad)}", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun EmptyState(icon: ImageVector, title: String, modifier: Modifier = Modifier, subtitle: String? = null) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        if (subtitle != null) {
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LoadingBox(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun BarRow(label: String, value: Int, max: Int, color: Color, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(96.dp)
        )
        Box(
            Modifier
                .weight(1f)
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(color.copy(alpha = 0.15f))
        ) {
            Box(
                Modifier
                    .fillMaxWidth(if (max > 0) (value.toFloat() / max).coerceIn(0f, 1f) else 0f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(7.dp))
                    .background(color)
            )
        }
        Text(
            "$value",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = Modifier.width(44.dp)
        )
    }
}

val ListaPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp)
