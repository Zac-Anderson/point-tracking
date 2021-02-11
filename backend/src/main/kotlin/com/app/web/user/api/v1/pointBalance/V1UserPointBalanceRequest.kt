package com.app.web.user.api.v1.pointBalance

import com.domain.user.PointBalanceLedger
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class V1UserPointBalanceRequest(
    @JsonProperty("payer")
    val payer: String,

    @JsonProperty("points")
    val points: Int,

    @JsonProperty("transaction_date")
    val transactionDate: Date
)

object V1UserPointBalanceRequestTranslator {
    fun translate(request: V1UserPointBalanceRequest) =
        PointBalanceLedger(
            payer = request.payer,
            points = request.points,
            transactionDate = request.transactionDate
        )
}