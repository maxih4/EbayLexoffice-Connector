package model.ebay


import kotlinx.serialization.Serializable

@Serializable
data class TotalDueSeller (

var value    : String? = null,
var currency : String? = null

)