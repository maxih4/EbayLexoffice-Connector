package model.ebay


import kotlinx.serialization.Serializable

@Serializable
data class LineItemFulfillmentInstructions (

var minEstimatedDeliveryDate : String?  = null,
var maxEstimatedDeliveryDate : String?  = null,
var shipByDate               : String?  = null,
var guaranteedDelivery       : Boolean? = null

)