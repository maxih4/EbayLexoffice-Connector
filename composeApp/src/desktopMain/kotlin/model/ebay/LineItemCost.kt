package model.ebay


import kotlinx.serialization.Serializable

@Serializable
data class LineItemCost (

var value    : String? = null,
var currency : String? = null

)