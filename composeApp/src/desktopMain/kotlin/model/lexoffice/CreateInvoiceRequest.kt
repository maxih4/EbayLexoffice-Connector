package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateInvoiceRequest(
    @SerialName("address")
    val address: Address? = Address(),
    @SerialName("archived")
    val archived: Boolean? = false,
    @SerialName("introduction")
    val introduction: String? = "",
    @SerialName("lineItems")
    val lineItems: List<LineItem?>? = listOf(),
    @SerialName("paymentConditions")
    val paymentConditions: PaymentConditions? = PaymentConditions(),
    @SerialName("remark")
    val remark: String? = "",
    @SerialName("shippingConditions")
    val shippingConditions: ShippingConditions? = ShippingConditions(),
    @SerialName("taxConditions")
    val taxConditions: TaxConditions? = TaxConditions(),
    @SerialName("title")
    val title: String? = "",
    @SerialName("totalPrice")
    val totalPrice: TotalPrice? = TotalPrice(),
    @SerialName("voucherDate")
    val voucherDate: String? = ""
)