package mx.edu.utez.proyectointegrador.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import kotlin.math.max

object ImageUtils {
    fun comprimir(context: Context, uri: Uri, ladoMaximo: Int = 1024): ByteArray? {
        val resolver = context.contentResolver
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, limites) }
        if (limites.outWidth <= 0 || limites.outHeight <= 0) return null

        var muestra = 1
        while (max(limites.outWidth, limites.outHeight) / muestra > ladoMaximo * 2) muestra *= 2
        val opciones = BitmapFactory.Options().apply { inSampleSize = muestra }
        val original = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) }
            ?: return null

        val rotacion = resolver.openInputStream(uri)?.use {
            when (ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } ?: 0f

        val escala = ladoMaximo.toFloat() / max(original.width, original.height)
        val matriz = Matrix().apply {
            if (escala < 1f) postScale(escala, escala)
            if (rotacion != 0f) postRotate(rotacion)
        }
        val finalBmp = Bitmap.createBitmap(original, 0, 0, original.width, original.height, matriz, true)
        val salida = ByteArrayOutputStream()
        finalBmp.compress(Bitmap.CompressFormat.JPEG, 80, salida)
        return salida.toByteArray()
    }
}
