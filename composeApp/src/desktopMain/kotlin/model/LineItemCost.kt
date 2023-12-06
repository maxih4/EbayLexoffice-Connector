package model


import kotlinx.serialization.Serializable

@Serializable
data class LineItemCost (

var value    : String? = null,
var currency : String? = null

)