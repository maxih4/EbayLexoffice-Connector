package model.ebay

import kotlinx.serialization.Serializable

@Serializable
data class AppliedPromotion(
    val discountAmount: DiscountAmount,
    val promotionId: String? = null,
    val description: String? =null,
)