package model.ebay

import kotlinx.serialization.Serializable

@Serializable
data class DiscountAmount(
    val value: String,
    val currency: String,
)