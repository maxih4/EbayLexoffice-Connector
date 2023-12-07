package model.ebay


import kotlinx.serialization.Serializable

@Serializable
data class BuyerRegistrationAddress (

    var fullName       : String?         = null,
    var contactAddress : ContactAddress? = ContactAddress(),
    var primaryPhone   : PrimaryPhone?   = PrimaryPhone(),
    var email          : String?         = null,
    var secondaryPhone: SecondaryPhone? = SecondaryPhone()

)