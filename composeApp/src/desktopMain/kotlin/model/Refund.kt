package model

import kotlinx.serialization.Serializable

@Serializable
data class Refund(
    var refundDate: String?=null,
    var amount: Amount?=Amount(),
    var refundStatus: String?=null,
    var refundReferenceId: String?=null,
    var refundId: String?=null,
)