package model.ebay

import kotlinx.serialization.Serializable

@Serializable


data class Buyer (

    var username                 : String?                   = null,
    var taxAddress               : TaxAddress?               = TaxAddress(),
    var buyerRegistrationAddress : BuyerRegistrationAddress? = BuyerRegistrationAddress()

)