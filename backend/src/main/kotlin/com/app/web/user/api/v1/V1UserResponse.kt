package com.app.web.user.api.v1

import com.app.web.user.api.v1.pointBalance.V1UserPointBalanceResponse
import com.app.web.user.api.v1.pointBalance.V1UserPointBalanceResponseTranslator
import com.domain.user.User

data class V1UserResponse(
    val pointBalance: List<V1UserPointBalanceResponse>
)

object V1UserResponseTranslator {
    fun translate(user: User) =
        V1UserResponse(
            pointBalance = user.pointBalance.map(V1UserPointBalanceResponseTranslator::translate)
        )
}