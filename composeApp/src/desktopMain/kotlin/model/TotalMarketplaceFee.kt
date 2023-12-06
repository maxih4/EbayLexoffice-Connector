package model


import kotlinx.serialization.Serializable

@Serializable
data class TotalMarketplaceFee (

var value    : String? = null,
var currency : String? = null

)