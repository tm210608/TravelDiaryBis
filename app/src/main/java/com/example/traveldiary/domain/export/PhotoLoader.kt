package com.example.traveldiary.domain.export

data class LoadedPhoto(
    val bytes: ByteArray,
    val extension: String,
) {
    override fun equals(other: Any?): Boolean =
        other is LoadedPhoto && bytes.contentEquals(other.bytes) && extension == other.extension

    override fun hashCode(): Int = 31 * bytes.contentHashCode() + extension.hashCode()
}

fun interface PhotoLoader {
    fun load(imageUrl: String): LoadedPhoto?
}
