package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Roles(
    @SerialName("customer")
    val customer: Customer? = Customer()
)