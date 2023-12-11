package model.ebay

import kotlinx.serialization.Serializable

@Serializable
data class Taxes(
    val amount: Amount? = Amount(),
)
