package model.ebay


import kotlinx.serialization.Serializable

@Serializable
data class FulfillmentStartInstructions (

var fulfillmentInstructionsType : String?       = null,
var minEstimatedDeliveryDate    : String?       = null,
var maxEstimatedDeliveryDate    : String?       = null,
var ebaySupportedFulfillment    : Boolean?      = null,
var shippingStep                : ShippingStep? = ShippingStep()

)