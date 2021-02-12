package com.app.web.user.api.v1.pointBalance

import com.domain.user.PointBalanceLedger
import java.util.*

data class V1UserPointDeductionResponse(
    val payer: String,
    val points: Int,
    val date: Date
)

object V1UserPointDeductionResponseTranslator {
    fun translate(pointBalanceLedger: PointBalanceLedger) =
        V1UserPointDeductionResponse(
            payer = pointBalanceLedger.payer,
            points = pointBalanceLedger.points,
            date = pointBalanceLedger.transactionDate
        )
}
