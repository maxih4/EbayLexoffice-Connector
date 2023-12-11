package model.ebay


import kotlinx.serialization.Serializable

@Serializable
data class CancelStatus (

var cancelState    : String?           = null,
var cancelRequests : ArrayList<CancelRequest> = arrayListOf()

)