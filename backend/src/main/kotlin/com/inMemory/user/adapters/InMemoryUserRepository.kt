package com.inMemory.user.adapters

import com.domain.user.PointBalanceLedger
import com.domain.user.User
import com.domain.user.ports.UserRepository
import com.inMemory.PointsNotUpdatedException
import com.inMemory.user.InMemoryUser
import com.inMemory.user.InMemoryUser.InMemoryPointBalance
import com.inMemory.user.InMemoryUserTranslator
import com.inMemory.user.UserCache
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.LocalDateTime
import kotlin.math.absoluteValue

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

    override fun deductPoints(points: Int): List<PointBalanceLedger> {
        val user = findOrCreateInMemoryUser()
        if (user.getTotalPoints() < points) throw PointsNotUpdatedException("Not enough total points in balance")

        val ledger = calculatePayerPointDeductions(user, points).map {
            PointBalanceLedger(
                it.key, it.value, Timestamp.valueOf(LocalDateTime.now())
            )
        }

        addPoints(ledger)

        return ledger.filter { it.points != 0 }
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

    private fun verifyPointLedgerAddition(
        user: InMemoryUser,
        pointBalanceLedger: List<PointBalanceLedger>
    ): List<InMemoryPointBalance> {
        val existingPointBalanceList = user.pointBalance.toMutableList()

        val pointBalanceLedgerUpdateList = pointBalanceLedger.map {
            InMemoryPointBalance(
                payer = it.payer,
                points = it.points,
                transactionDate = it.transactionDate
            )
        }
        existingPointBalanceList.addAll(pointBalanceLedgerUpdateList)

        val unvalidatedList = existingPointBalanceList.toList().sortedWith(compareBy { it.transactionDate })
        val inProgressList = mutableListOf<InMemoryPointBalance>()

        unvalidatedList.forEach { record ->
            if (record.points > 0) {
                inProgressList.add(record)
            } else {
                var pointsToSubtract = record.points.absoluteValue
                val payerRecords = inProgressList.toList().filter { it.payer.equals(record.payer, ignoreCase = true) }

                for (value in payerRecords) {
                    if (pointsToSubtract >= value.points) {
                        inProgressList.remove(value)
                        inProgressList.add(value.copy(points = 0))
                        pointsToSubtract -= value.points
                    } else if (pointsToSubtract < value.points && pointsToSubtract != 0) {
                        inProgressList.remove(value)
                        inProgressList.add(value.copy(points = value.points - pointsToSubtract))
                        pointsToSubtract = 0
                        break
                    }
                }

                if (pointsToSubtract != 0) throw PointsNotUpdatedException("${record.payer.toUpperCase()} can't go below 0 points")
            }
        }

        return inProgressList.toList().sortedWith(compareBy { it.transactionDate })
    }

    private fun calculatePayerPointDeductions(user: InMemoryUser, points: Int): HashMap<String, Int> {
        val map = HashMap<String, Int>()
        var remainingPoints = points

        for (record in user.pointBalance) {
            if (remainingPoints - record.points > 0) {
                remainingPoints -= record.points
                when (map[record.payer.toUpperCase()]) {
                    null -> map[record.payer.toUpperCase()] = -record.points
                    else -> map[record.payer.toUpperCase()] = map[record.payer.toUpperCase()]!! - record.points
                }
            } else {
                when (map[record.payer.toUpperCase()]) {
                    null -> map[record.payer.toUpperCase()] = -remainingPoints
                    else -> map[record.payer.toUpperCase()] = map[record.payer.toUpperCase()]!! - remainingPoints
                }

                break
            }
        }

        return map
    }
}
