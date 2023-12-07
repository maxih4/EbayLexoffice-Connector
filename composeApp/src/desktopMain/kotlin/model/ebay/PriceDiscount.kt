package model.ebay

import kotlinx.serialization.Serializable

@Serializable
data class PriceDiscount(
    var value: String?=null,
    var currency: String?=null,
)