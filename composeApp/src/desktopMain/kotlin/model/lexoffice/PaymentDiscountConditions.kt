package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentDiscountConditions(
    @SerialName("discountPercentage")
    val discountPercentage: Int? = 0,
    @SerialName("discountRange")
    val discountRange: Int? = 0
)