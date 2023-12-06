package model


import kotlinx.serialization.Serializable

@Serializable
data class TaxAddress (

var postalCode  : String? = null,
var countryCode : String? = null

)