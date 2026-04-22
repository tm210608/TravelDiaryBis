package com.example.traveldiary.data.util

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Clase encargada de gestionar la persistencia de imágenes en el almacenamiento interno de la app.
 */
@Singleton
class ImageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val directoryName = "travel_images"

    /**
     * Guarda una imagen desde una URI temporal a una ubicación permanente en filesDir.
     * @param tempUri URI de la imagen (usualmente de la cámara o caché).
     * @return El nombre del archivo guardado o null si falla.
     */
    fun saveImagePermanently(tempUri: Uri): String? {
        return try {
            val directory = File(context.filesDir, directoryName)
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val fileName = "IMG_${UUID.randomUUID()}.jpg"
            val permanentFile = File(directory, fileName)

            context.contentResolver.openInputStream(tempUri)?.use { inputStream ->
                FileOutputStream(permanentFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            fileName
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Obtiene la ruta absoluta de una imagen guardada permanentemente.
     */
    fun getImagePath(fileName: String): String {
        return File(File(context.filesDir, directoryName), fileName).absolutePath
    }

    /**
     * Elimina una imagen del almacenamiento permanente.
     */
    fun deleteImage(fileName: String): Boolean {
        return try {
            val file = File(File(context.filesDir, directoryName), fileName)
            if (file.exists()) file.delete() else false
        } catch (e: Exception) {
            false
        }
    }
}
