package model.ebay

import kotlinx.serialization.Serializable

@Serializable
data class PriceSubtotal (

var value    : String? = null,
var currency : String? = null

)