package model


import kotlinx.serialization.Serializable

@Serializable
data class ContactAddress(

    var addressLine1: String? = null,
    var city: String? = null,
    var postalCode: String? = null,
    var countryCode: String? = null,
    var stateOrProvince: String? = null,
    var addressLine2: String? = null

)