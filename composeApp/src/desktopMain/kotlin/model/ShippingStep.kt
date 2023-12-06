package model


import kotlinx.serialization.Serializable

@Serializable
data class ShippingStep (

var shipTo              : ShipTo? = ShipTo(),
var shippingCarrierCode : String? = null,
var shippingServiceCode : String? = null

)