package com.example.traveldiary.domain.model

import java.time.Instant
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportPdfFileNameTest {

    @Test
    fun `exportPdfFileName uses diary prefix and pdf extension`() {
        val name = exportPdfFileName(Instant.parse("2026-11-11T18:30:00Z"))
        assertTrue(Regex("traveldiary-diario-\\d{8}-\\d{4}\\.pdf").matches(name))
    }

    @Test
    fun `exportFileName keeps zip format`() {
        val name = exportFileName(Instant.parse("2026-11-11T18:30:00Z"))
        assertTrue(Regex("traveldiary-export-\\d{8}-\\d{4}\\.zip").matches(name))
    }
}
