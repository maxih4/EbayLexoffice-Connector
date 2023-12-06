package model

import kotlinx.serialization.Serializable

@Serializable
data class ShippingCost (

var value    : String? = null,
var currency : String? = null

)