package org.ppfc.api.model

import kotlinx.serialization.Serializable

@Serializable
data class GenerateChangesDocumentResponse(
    val fileName: String? = null,
    val fileBytes: ByteArray? = null,
    val error: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GenerateChangesDocumentResponse

        if (fileName != other.fileName) return false
        if (!fileBytes.contentEquals(other.fileBytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + fileBytes.contentHashCode()
        return result
    }
}
