package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LineItem(
    @SerialName("description")
    val description: String? = "",
    @SerialName("discountPercentage")
    val discountPercentage: Float? = 0F,
    @SerialName("name")
    val name: String? = "",
    @SerialName("quantity")
    val quantity: Int? = 0,
    @SerialName("type")
    val type: String? = "",
    @SerialName("unitName")
    val unitName: String? = "",
    @SerialName("unitPrice")
    val unitPrice: UnitPrice? = UnitPrice()
)