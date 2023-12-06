package model


import kotlinx.serialization.Serializable

@Serializable
data class CancelStatus (

var cancelState    : String?           = null,
var cancelRequests : ArrayList<String> = arrayListOf()

)