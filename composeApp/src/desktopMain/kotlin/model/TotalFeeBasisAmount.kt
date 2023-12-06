package model


import kotlinx.serialization.Serializable

@Serializable
data class TotalFeeBasisAmount (

var value    : String? = null,
var currency : String? = null

)