package com.inMemory.user.adapters

import com.domain.user.PointBalanceLedger
import com.domain.user.User
import com.domain.user.ports.UserRepository
import com.inMemory.PointsNotUpdatedException
import com.inMemory.user.InMemoryUser
import com.inMemory.user.InMemoryUser.*
import com.inMemory.user.InMemoryUserTranslator
import com.inMemory.user.UserCache
import org.springframework.stereotype.Component

@Component
class InMemoryUserRepository(
    private val userCache: UserCache
) : UserRepository {

    override fun find(): User {
        val user = findOrCreateInMemoryUser()
        return InMemoryUserTranslator.translate(user)
    }

    override fun addPoints(pointBalanceLedger: List<PointBalanceLedger>) {
        val user = findOrCreateInMemoryUser()
        val updatedPointBalance = verifyPointLedgerAddition(user, pointBalanceLedger)
        userCache["user"] = InMemoryUser(pointBalance = updatedPointBalance)
    }

    private fun findOrCreateInMemoryUser(): InMemoryUser {
        return when (userCache.size) {
            0 -> {
                userCache["user"] = InMemoryUser(pointBalance = emptyList())
                userCache["user"]!!
            }
            else -> userCache["user"]!!
        }
    }

    private fun verifyPointLedgerAddition(user: InMemoryUser, pointBalanceLedger: List<PointBalanceLedger>): List<InMemoryPointBalance> {
        val existingPointBalanceList = user.pointBalance.toMutableList()

        val pointBalanceLedgerUpdateList = pointBalanceLedger.map {
            InMemoryPointBalance(
                payer = it.payer,
                points = it.points,
                transactionDate = it.transactionDate
            )
        }
        existingPointBalanceList.addAll(pointBalanceLedgerUpdateList)

        val sortedList = existingPointBalanceList.toList().sortedWith(compareBy { it.transactionDate })

        val map = HashMap<String, Int>()

        sortedList.forEach { record ->
            when (map[record.payer.toUpperCase()]) {
                null -> map[record.payer.toUpperCase()] = record.points
                else -> map[record.payer.toUpperCase()] = map[record.payer.toUpperCase()]!! + record.points
            }

            if (map[record.payer.toUpperCase()]!! < 0) {
                throw PointsNotUpdatedException("${record.payer.toUpperCase()} can't go below 0 points")
            }
        }

        return sortedList
    }
}
