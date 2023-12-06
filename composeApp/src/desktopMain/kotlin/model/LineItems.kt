package model

import kotlinx.serialization.Serializable

@Serializable
data class LineItems(

    var lineItemId: String? = null,
    var legacyItemId: String? = null,
    var title: String? = null,
    var lineItemCost: LineItemCost? = LineItemCost(),
    var quantity: Int? = null,
    var soldFormat: String? = null,
    var listingMarketplaceId: String? = null,
    var purchaseMarketplaceId: String? = null,
    var lineItemFulfillmentStatus: String? = null,
    var total: Total? = Total(),
    var deliveryCost: DeliveryCost? = DeliveryCost(),
    var appliedPromotions: ArrayList<AppliedPromotion> = arrayListOf(),
    var taxes: ArrayList<String> = arrayListOf(),
    var properties: Properties? = Properties(),
    var lineItemFulfillmentInstructions: LineItemFulfillmentInstructions? = LineItemFulfillmentInstructions(),
    var itemLocation: ItemLocation? = ItemLocation(),
    val discountedLineItemCost: DiscountedLineItemCost? = DiscountedLineItemCost(),
    val legacyVariationId: String? = null,
    val variationAspects: List<VariationAspect>? = arrayListOf(),
    val refunds: List<Refund>? = arrayListOf(),

    )