package model.ebay

import kotlinx.serialization.Serializable

@Serializable
data class SecondaryPhone(
    val phoneNumber: String?=null,
)