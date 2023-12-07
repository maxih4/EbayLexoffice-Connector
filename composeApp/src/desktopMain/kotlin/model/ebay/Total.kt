package model.ebay

import kotlinx.serialization.Serializable

@Serializable

data class Total (

var value    : String? = null,
var currency : String? = null

)