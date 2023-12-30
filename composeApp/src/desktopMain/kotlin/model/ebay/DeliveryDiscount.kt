package model.ebay

import kotlinx.serialization.Serializable

@Serializable
data class DeliveryDiscount (


    var value: String?=null,
    var currency: String?=null,

    )
