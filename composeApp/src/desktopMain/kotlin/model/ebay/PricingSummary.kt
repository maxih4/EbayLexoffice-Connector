package model.ebay

import kotlinx.serialization.Serializable

@Serializable

data class PricingSummary (

    var priceSubtotal : PriceSubtotal? = PriceSubtotal(),
    var deliveryCost  : DeliveryCost?  = DeliveryCost(),
    var total         : Total?         = Total(),
    val priceDiscount: PriceDiscount? = PriceDiscount(),
    var deliveryDiscount: DeliveryDiscount? = DeliveryDiscount()

    )