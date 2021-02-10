package com.inMemory.user

import com.domain.user.User
import java.time.LocalDateTime

data class InMemoryUser(
    val pointBalance: List<InMemoryPointBalance>
) {
    data class InMemoryPointBalance(
        val payer: String,
        val points: Int,
        val transactionDate: LocalDateTime
    )
}

object InMemoryUserTranslator {
    fun translate(inMemoryUser: InMemoryUser): User =
        User(
            pointBalance = inMemoryUser.pointBalance.map { User.PointBalance(payer = it.payer, points = it.points) }
        )
}
