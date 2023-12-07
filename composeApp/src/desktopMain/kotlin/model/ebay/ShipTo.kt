package model.ebay


import kotlinx.serialization.Serializable

@Serializable
data class ShipTo (

    var fullName       : String?         = null,
    var contactAddress : ContactAddress? = ContactAddress(),
    var primaryPhone   : PrimaryPhone?   = PrimaryPhone(),
    var email          : String?         = null

)