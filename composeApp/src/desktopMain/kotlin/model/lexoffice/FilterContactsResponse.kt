package model.lexoffice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FilterContactsResponse(
    @SerialName("content")
    val content: List<Content?>? = listOf(),
    @SerialName("first")
    val first: Boolean? = false,
    @SerialName("last")
    val last: Boolean? = false,
    @SerialName("number")
    val number: Int? = 0,
    @SerialName("numberOfElements")
    val numberOfElements: Int? = 0,
    @SerialName("size")
    val size: Int? = 0,
    @SerialName("sort")
    val sort: List<Sort?>? = listOf(),
    @SerialName("totalElements")
    val totalElements: Int? = 0,
    @SerialName("totalPages")
    val totalPages: Int? = 0
)