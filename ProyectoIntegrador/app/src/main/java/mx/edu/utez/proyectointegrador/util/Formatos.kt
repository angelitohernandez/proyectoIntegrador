package mx.edu.utez.proyectointegrador.util

import mx.edu.utez.proyectointegrador.data.remote.dto.ProductoDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private val formatoVista = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun parseFecha(valor: String?): LocalDate? =
    valor?.takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

fun formatoFecha(valor: String?): String = parseFecha(valor)?.format(formatoVista) ?: "—"

fun diasParaCaducar(valor: String?): Long? =
    parseFecha(valor)?.let { ChronoUnit.DAYS.between(LocalDate.now(), it) }

fun formatoMoneda(valor: Double): String = "$" + String.format(Locale.US, "%,.2f", valor)

val ProductoDto.precioFinal: Double
    get() = precio * (100 - descuento) / 100.0

val ProductoDto.enPromocion: Boolean
    get() = descuento > 0

val ProductoDto.stockBajo: Boolean
    get() = stock <= stockMinimo

val ProductoDto.porCaducar: Boolean
    get() = (diasParaCaducar(caducidad) ?: Long.MAX_VALUE) <= 7

val ProductoDto.esCritico: Boolean
    get() = stockBajo || porCaducar
