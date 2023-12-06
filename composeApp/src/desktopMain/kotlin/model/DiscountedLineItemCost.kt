package model

import kotlinx.serialization.Serializable

@Serializable
data class DiscountedLineItemCost(
    var value: String?=null,
    var currency: String?=null,
)