package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentConditions(
    @SerialName("paymentDiscountConditions")
    val paymentDiscountConditions: PaymentDiscountConditions? = PaymentDiscountConditions(),
    @SerialName("paymentTermDuration")
    val paymentTermDuration: Int? = 0,
    @SerialName("paymentTermLabel")
    val paymentTermLabel: String? = ""
)