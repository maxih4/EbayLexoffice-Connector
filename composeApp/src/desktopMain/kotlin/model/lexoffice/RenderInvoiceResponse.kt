package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RenderInvoiceResponse(
    @SerialName("documentFileId")
    val documentFileId: String? = ""
)