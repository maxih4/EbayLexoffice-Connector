package model.ebay


import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse (

var href   : String?           = null,
var total  : Int?              = null,
var limit  : Int?              = null,
var offset : Int?              = null,
var orders : ArrayList<Orders> = arrayListOf(),
    var next: String?=null

)