package model

import kotlinx.serialization.Serializable

@Serializable
data class AppliedPromotion(
    val discountAmount: DiscountAmount,
    val promotionId: String,
    val description: String,
)