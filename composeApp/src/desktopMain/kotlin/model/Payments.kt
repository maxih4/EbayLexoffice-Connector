package model

import kotlinx.serialization.Serializable

@Serializable
data class Payments (

var paymentMethod      : String? = null,
var paymentReferenceId : String? = null,
var paymentDate        : String? = null,
var amount             : Amount? = Amount(),
var paymentStatus      : String? = null

)