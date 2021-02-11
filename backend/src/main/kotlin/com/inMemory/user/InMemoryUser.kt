package com.inMemory.user

import com.domain.user.User
import com.domain.user.User.PointBalance
import java.util.*
import kotlin.collections.LinkedHashMap

data class InMemoryUser(
    val pointBalance: List<InMemoryPointBalance>
) {
    data class InMemoryPointBalance(
        val payer: String,
        val points: Int,
        val transactionDate: Date
    )

    fun getPointsOverview(): LinkedHashMap<String, Int> {
        val map = LinkedHashMap<String, Int>()

        this.pointBalance.forEach { record ->
            when (map[record.payer.toUpperCase()]) {
                null -> map[record.payer.toUpperCase()] = record.points
                else -> map[record.payer.toUpperCase()] = map[record.payer.toUpperCase()]!! + record.points
            }
        }

        return map
    }
}

object InMemoryUserTranslator {
    fun translate(inMemoryUser: InMemoryUser) = User(
        pointBalance = inMemoryUser.getPointsOverview().map {
            PointBalance(payer = it.key, points = it.value)
        }
    )
}
