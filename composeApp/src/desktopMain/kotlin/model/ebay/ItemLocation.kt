package model.ebay

import kotlinx.serialization.Serializable

@Serializable


data class ItemLocation (

var location    : String? = null,
var countryCode : String? = null,
var postalCode  : String? = null

)