package model.ebay

import kotlinx.serialization.Serializable

@Serializable
data class Orders (

    var orderId                      : String?                                 = null,
    var legacyOrderId                : String?                                 = null,
    var creationDate                 : String?                                 = null,
    var lastModifiedDate             : String?                                 = null,
    var orderFulfillmentStatus       : String?                                 = null,
    var orderPaymentStatus           : String?                                 = null,
    var sellerId                     : String?                                 = null,
    var buyer                        : Buyer?                                  = Buyer(),
    var pricingSummary               : PricingSummary?                         = PricingSummary(),
    var cancelStatus                 : CancelStatus?                           = CancelStatus(),
    var paymentSummary               : PaymentSummary?                         = PaymentSummary(),
    var fulfillmentStartInstructions : ArrayList<FulfillmentStartInstructions> = arrayListOf(),
    var fulfillmentHrefs             : ArrayList<String>                       = arrayListOf(),
    var lineItems                    : ArrayList<LineItems>                    = arrayListOf(),
    var salesRecordReference         : String?                                 = null,
    var totalFeeBasisAmount          : TotalFeeBasisAmount?                    = TotalFeeBasisAmount(),
    var totalMarketplaceFee          : TotalMarketplaceFee?                    = TotalMarketplaceFee()

)