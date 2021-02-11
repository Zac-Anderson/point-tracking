package com.app.web.user.api.v1.pointBalance

import com.domain.user.User

data class V1UserPointBalanceResponse(
    val payer: String,
    val points: Int
)

object V1UserPointBalanceResponseTranslator {
    fun translate(pointBalance: User.PointBalance) =
        V1UserPointBalanceResponse(
            payer = pointBalance.payer,
            points = pointBalance.points
        )
}