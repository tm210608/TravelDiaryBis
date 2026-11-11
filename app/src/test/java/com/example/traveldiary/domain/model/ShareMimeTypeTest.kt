package com.example.traveldiary.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ShareMimeTypeTest {

    @Test
    fun `pdf extension maps to application pdf`() {
        assertEquals("application/pdf", shareMimeType("traveldiary-diario-20261111-1830.pdf"))
        assertEquals("application/pdf", shareMimeType("A.PDF"))
    }

    @Test
    fun `zip extension maps to application zip`() {
        assertEquals("application/zip", shareMimeType("traveldiary-export-20261111-1830.zip"))
        assertEquals("application/zip", shareMimeType("A.ZIP"))
    }

    @Test
    fun `other extensions fall back to octet stream`() {
        assertEquals("application/octet-stream", shareMimeType("diario.txt"))
        assertEquals("application/octet-stream", shareMimeType("sinextension"))
    }
}
