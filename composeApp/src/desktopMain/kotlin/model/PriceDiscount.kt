package model

import kotlinx.serialization.Serializable

@Serializable
data class PriceDiscount(
    var value: String?=null,
    var currency: String?=null,
)