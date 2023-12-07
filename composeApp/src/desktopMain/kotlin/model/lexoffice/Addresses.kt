package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Addresses(
    @SerialName("billing")
    val billing: List<Billing?>? = listOf()
)