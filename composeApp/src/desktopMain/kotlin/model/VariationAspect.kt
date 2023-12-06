package model

import kotlinx.serialization.Serializable

@Serializable
data class VariationAspect(
    var name: String?=null,
    var value: String?=null,
)