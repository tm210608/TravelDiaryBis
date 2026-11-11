package com.example.traveldiary.data.export

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.traveldiary.domain.export.LoadedPhoto
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.model.buildPdfCover
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.Instant
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PdfDocumentExporterTest {

    @Test
    fun generatedPdf_hasCoverPlusOnePagePerEntry() = runBlocking {
        val entries = listOf(
            TravelEntry(
                id = 1, title = "Atardecer en Kyoto", location = "Kioto", country = "Japón",
                date = Instant.now().minusSeconds(2 * 86400), tag = "Ciudad",
                imageUrl = "file:///foto.jpg", description = "Un atardecer increíble.",
            ),
            TravelEntry(
                id = 2, title = "Entrada sin foto", location = "", country = "",
                date = Instant.now(), tag = "", imageUrl = "",
            ),
        )
        val photo = LoadedPhoto(jpegBytes(), "jpg")
        val out = ByteArrayOutputStream()

        PdfDocumentExporter().export(
            entries, buildPdfCover(entries), { photo }, out, Instant.now()
        )

        val bytes = out.toByteArray()
        assertEquals("%PDF", String(bytes, 0, 4))

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File(context.cacheDir, "test-diary.pdf")
        file.writeBytes(bytes)
        val renderer = PdfRenderer(
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        )
        renderer.use {
            assertEquals(3, it.pageCount)
            val page = it.openPage(0)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            assertTrue(bitmap.width > 0)
            assertTrue(bitmap.height > 0)
            bitmap.recycle()
        }
    }

    private fun jpegBytes(): ByteArray {
        val bmp = Bitmap.createBitmap(40, 30, Bitmap.Config.ARGB_8888)
        bmp.eraseColor(Color.RED)
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, baos)
        bmp.recycle()
        return baos.toByteArray()
    }
}
