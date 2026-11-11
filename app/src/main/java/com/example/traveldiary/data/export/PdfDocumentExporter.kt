package com.example.traveldiary.data.export

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import com.example.traveldiary.domain.export.LoadedPhoto
import com.example.traveldiary.domain.export.PdfExporter
import com.example.traveldiary.domain.model.PdfCoverData
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.model.formatKm
import com.example.traveldiary.domain.model.formatToTicket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.time.Instant
import java.util.Locale
import javax.inject.Inject

class PdfDocumentExporter
    @Inject
    constructor() : PdfExporter {
        override suspend fun export(
            entries: List<TravelEntry>,
            cover: PdfCoverData,
            photoFor: (String) -> LoadedPhoto?,
            out: OutputStream,
            exportedAt: Instant,
        ) = withContext(Dispatchers.IO) {
            val document = PdfDocument()
            try {
                val totalPages = entries.size + 1
                drawPage(document, 1) { drawCover(it, cover, exportedAt, totalPages) }
                entries.forEachIndexed { index, entry ->
                    val page = index + 2
                    drawPage(document, page) {
                        drawEntry(it, entry, photoFor, index + 1, page, totalPages)
                    }
                }
                document.writeTo(out)
            } finally {
                document.close()
            }
        }

        private inline fun drawPage(
            document: PdfDocument,
            pageNumber: Int,
            block: (Canvas) -> Unit,
        ) {
            val page =
                document.startPage(
                    PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create(),
                )
            try {
                page.canvas.drawColor(PAPER)
                block(page.canvas)
            } finally {
                document.finishPage(page)
            }
        }

        private fun drawCover(
            canvas: Canvas,
            cover: PdfCoverData,
            exportedAt: Instant,
            totalPages: Int,
        ) {
            val mono = textPaint(11f, VERMILION, mono = true)
            val meta = textPaint(12f, INK_VARIANT, mono = true)

            canvas.drawText("REGISTRO EXPEDICIONARIO", MARGIN, 150f, mono)
            canvas.drawText("DIARIO DE VIAJE", MARGIN, 200f, textPaint(34f, INK, bold = true))
            drawDashedRule(canvas, 226f)
            canvas.drawText(
                "VIAJES %02d · FAV %02d · PAÍSES %02d · CONT %02d".format(
                    Locale.US,
                    cover.trips,
                    cover.favourites,
                    cover.countries,
                    cover.continents,
                ),
                MARGIN,
                258f,
                meta,
            )

            canvas.drawText("KM RECORRIDOS", MARGIN, 420f, mono)
            canvas.drawText(formatKm(cover.kmTotal), MARGIN, 480f, textPaint(48f, INK, bold = true, mono = true))
            val kmWidth =
                textPaint(48f, INK, bold = true, mono = true)
                    .measureText(formatKm(cover.kmTotal))
            canvas.drawText("KM", MARGIN + kmWidth + 10f, 480f, meta)
            canvas.drawText(
                "%02d TRAMOS · %02d DÍAS EN RUTA".format(Locale.US, cover.legs, cover.daysOnRoute),
                MARGIN,
                512f,
                meta,
            )

            val generated = exportedAt.formatToTicket()
            val footer = "GENERADO OFFLINE · ${generated.day} ${generated.month} ${generated.year}"
            canvas.drawText(footer, centeredX(footer, meta), PAGE_HEIGHT - 90f, meta)
            drawFooter(canvas, 1, totalPages)
        }

        private fun drawEntry(
            canvas: Canvas,
            entry: TravelEntry,
            photoFor: (String) -> LoadedPhoto?,
            entryIndex: Int,
            page: Int,
            totalPages: Int,
        ) {
            val photoRect =
                Rect(
                    MARGIN.toInt(),
                    MARGIN.toInt(),
                    (PAGE_WIDTH - MARGIN).toInt(),
                    (MARGIN + PHOTO_HEIGHT).toInt(),
                )
            val bitmap =
                entry.imageUrl
                    .takeIf { it.isNotBlank() && !it.startsWith("http") }
                    ?.let(photoFor)
                    ?.let { decodePhoto(it.bytes) }
            if (bitmap != null) {
                drawCenterCrop(canvas, bitmap, photoRect)
                bitmap.recycle()
            } else {
                canvas.drawRect(photoRect, Paint().apply { color = PLACEHOLDER })
                val label = "SIN FOTO"
                val labelPaint = textPaint(12f, INK_VARIANT, mono = true)
                canvas.drawText(
                    label,
                    photoRect.centerX() - labelPaint.measureText(label) / 2f,
                    photoRect.centerY() + 4f,
                    labelPaint,
                )
            }
            canvas.drawRect(
                photoRect,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    color = RULE
                    strokeWidth = 1f
                },
            )

            var y = photoRect.bottom + 34f
            val date = entry.date.formatToTicket()
            val eyebrow =
                buildString {
                    append("ENTRADA %02d · %s %s %s".format(Locale.US, entryIndex, date.day, date.month, date.year))
                    if (entry.country.isNotBlank()) append(" · ${entry.country.trim().uppercase(Locale.ROOT)}")
                }
            canvas.drawText(eyebrow, MARGIN, y, textPaint(10.5f, VERMILION, mono = true))
            y += 30f

            val contentWidth = (PAGE_WIDTH - MARGIN * 2).toInt()
            val titleLayout =
                StaticLayout.Builder
                    .obtain(entry.title, 0, entry.title.length, textPaint(22f, INK, bold = true), contentWidth)
                    .setMaxLines(2)
                    .setEllipsize(TextUtils.TruncateAt.END)
                    .build()
            canvas.save()
            canvas.translate(MARGIN, y)
            titleLayout.draw(canvas)
            canvas.restore()
            y += titleLayout.height + 14f

            val meta =
                listOf(entry.location, entry.tag)
                    .filter { it.isNotBlank() }
                    .joinToString(" · ") { it.trim().uppercase(Locale.ROOT) }
            if (meta.isNotEmpty()) {
                canvas.drawText(meta, MARGIN, y, textPaint(10f, INK_VARIANT, mono = true))
                y += 24f
            }

            if (entry.description.isNotBlank()) {
                val bodyPaint = textPaint(11.5f, INK)
                val available = PAGE_HEIGHT - FOOTER_ZONE - y
                val maxLines = (available / (bodyPaint.textSize * 1.35f)).toInt().coerceAtLeast(1)
                val bodyLayout =
                    StaticLayout.Builder
                        .obtain(entry.description, 0, entry.description.length, bodyPaint, contentWidth)
                        .setMaxLines(maxLines)
                        .setEllipsize(TextUtils.TruncateAt.END)
                        .build()
                canvas.save()
                canvas.translate(MARGIN, y)
                bodyLayout.draw(canvas)
                canvas.restore()
            }

            drawFooter(canvas, page, totalPages)
        }

        private fun drawCenterCrop(
            canvas: Canvas,
            bitmap: Bitmap,
            rect: Rect,
        ) {
            val scale =
                maxOf(
                    rect.width() / bitmap.width.toFloat(),
                    rect.height() / bitmap.height.toFloat(),
                )
            val w = bitmap.width * scale
            val h = bitmap.height * scale
            val dst =
                RectF(
                    rect.centerX() - w / 2f,
                    rect.centerY() - h / 2f,
                    rect.centerX() + w / 2f,
                    rect.centerY() + h / 2f,
                )
            canvas.save()
            canvas.clipRect(rect)
            canvas.drawBitmap(bitmap, null, dst, null)
            canvas.restore()
        }

        private fun decodePhoto(bytes: ByteArray): Bitmap? {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
            var sample = 1
            while (bounds.outWidth / (sample * 2) >= MAX_PHOTO_DIM &&
                bounds.outHeight / (sample * 2) >= MAX_PHOTO_DIM
            ) {
                sample *= 2
            }
            return BitmapFactory.decodeByteArray(
                bytes,
                0,
                bytes.size,
                BitmapFactory.Options().apply { inSampleSize = sample },
            )
        }

        private fun drawDashedRule(
            canvas: Canvas,
            y: Float,
        ) {
            canvas.drawLine(
                MARGIN,
                y,
                PAGE_WIDTH - MARGIN,
                y,
                Paint().apply {
                    color = RULE
                    strokeWidth = 1f
                    pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 0f)
                },
            )
        }

        private fun drawFooter(
            canvas: Canvas,
            page: Int,
            totalPages: Int,
        ) {
            val paint = textPaint(9f, INK_VARIANT, mono = true)
            val text = "PÁGINA %02d DE %02d".format(Locale.US, page, totalPages)
            canvas.drawText(text, centeredX(text, paint), PAGE_HEIGHT - 40f, paint)
        }

        private fun centeredX(
            text: String,
            paint: TextPaint,
        ): Float = (PAGE_WIDTH - paint.measureText(text)) / 2f

        private fun textPaint(
            size: Float,
            color: Int,
            bold: Boolean = false,
            mono: Boolean = false,
        ) = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = size
            this.color = color
            typeface =
                when {
                    mono -> Typeface.MONOSPACE
                    bold -> Typeface.DEFAULT_BOLD
                    else -> Typeface.DEFAULT
                }
        }

        private companion object {
            const val PAGE_WIDTH = 595
            const val PAGE_HEIGHT = 842
            const val MARGIN = 48f
            const val PHOTO_HEIGHT = 380f
            const val FOOTER_ZONE = 70f
            const val MAX_PHOTO_DIM = 1600

            val PAPER: Int = Color.rgb(0xFB, 0xF7, 0xEF)
            val INK: Int = Color.rgb(0x14, 0x1B, 0x24)
            val INK_VARIANT: Int = Color.rgb(0x5C, 0x64, 0x70)
            val VERMILION: Int = Color.rgb(0xC8, 0x3E, 0x2B)
            val RULE: Int = Color.rgb(0xD8, 0xCF, 0xC0)
            val PLACEHOLDER: Int = Color.rgb(0xE4, 0xDA, 0xCA)
        }
    }
