package model

import kotlinx.serialization.Serializable


@Serializable
data class Amount (

var value    : String? = null,
var currency : String? = null

)