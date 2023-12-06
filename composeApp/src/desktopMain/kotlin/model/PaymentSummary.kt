package model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentSummary (

var totalDueSeller : TotalDueSeller?     = TotalDueSeller(),
var refunds        : ArrayList<Refund>   = arrayListOf(),
var payments       : ArrayList<Payments> = arrayListOf()

)