package model.ebay


import kotlinx.serialization.Serializable

@Serializable
data class DeliveryCost (

    var shippingCost : ShippingCost? = ShippingCost(),
    var value: String?=null,
    var currency: String?=null,

    )