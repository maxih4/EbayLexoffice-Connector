package model.ebay

import kotlinx.serialization.Serializable

@Serializable
data class CancelRequest(
    var cancelReason: String? = null,
    var cancelRequestDate: String? = null,
    var cancelInitiator: String? = null,
    var cancelRequestId: String? = null,
    val cancelRequestState: String? = null


)