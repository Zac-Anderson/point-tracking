package com.domain.user.ports

import com.domain.user.PointBalanceLedger
import com.domain.user.User
import com.inMemory.PointsNotUpdatedException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.sql.Timestamp

abstract class UserRepositoryContractTest {
    private lateinit var subject: UserRepository

    @Before
    fun setUp() {
        subject = buildSubject()
    }

    abstract fun buildSubject(): UserRepository

    @Test
    fun `find returns the user`() {
        val result = subject.find()

        assertThat(result).isInstanceOf(User::class.java)
    }

    @Test
    fun `addPoints will add points to the user point balance`() {
        val dannonAddition = PointBalanceLedger("dannon", 400, Timestamp.valueOf("2020-02-11 12:00:00"))
        val unileverAddition = PointBalanceLedger("unilever", 300, Timestamp.valueOf("2021-02-11 12:00:00"))
        val dannonSecondAddition = PointBalanceLedger("dannon", 500, Timestamp.valueOf("2020-02-11 12:00:00"))

        subject.addPoints(listOf(dannonAddition, unileverAddition, dannonSecondAddition))

        val result = subject.find()

        assertThat(result.pointBalance).containsExactly(
            User.PointBalance("DANNON", 900),
            User.PointBalance("UNILEVER", 300)
        )
    }

    @Test
    fun `addPoints will subtract points from the user point balance when a negative is used`() {
        val dannonAddition = PointBalanceLedger("dannon", 900, Timestamp.valueOf("2020-02-11 12:00:00"))
        val unileverAddition = PointBalanceLedger("unilever", 300, Timestamp.valueOf("2021-02-11 12:00:00"))
        val dannonSecondAddition = PointBalanceLedger("dannon", -500, Timestamp.valueOf("2021-02-11 12:00:00"))

        subject.addPoints(listOf(dannonAddition, unileverAddition, dannonSecondAddition))

        val result = subject.find()

        assertThat(result.pointBalance).containsExactly(
            User.PointBalance("DANNON", 400),
            User.PointBalance("UNILEVER", 300)
        )
    }

    @Test
    fun `addPoints will throw an error if it tries to reduce a payer's points to below 0`() {
        val dannonAddition = PointBalanceLedger("dannon", 400, Timestamp.valueOf("2020-02-11 12:00:00"))
        val unileverAddition = PointBalanceLedger("unilever", 300, Timestamp.valueOf("2021-02-11 12:00:00"))
        val dannonSecondAddition = PointBalanceLedger("dannon", -500, Timestamp.valueOf("2021-02-11 12:00:00"))

        try {
            subject.addPoints(listOf(dannonAddition, unileverAddition, dannonSecondAddition))
        } catch (ex: PointsNotUpdatedException) {
            val result = subject.find()

            assertThat(result.pointBalance).isEmpty()
        }
    }

    @Test
    fun `deductPoints will subtract points from the user point balance taking oldest points first`() {
        val dannonAddition = PointBalanceLedger("dannon", 400, Timestamp.valueOf("2020-02-11 12:00:00"))
        val unileverAddition = PointBalanceLedger("unilever", 300, Timestamp.valueOf("2021-01-11 12:00:00"))
        val dannonSecondAddition = PointBalanceLedger("dannon", 500, Timestamp.valueOf("2021-02-11 12:00:00"))

        subject.addPoints(listOf(dannonAddition, unileverAddition, dannonSecondAddition))
        val result = subject.deductPoints(500)

        assertThat(result[0]).isEqualTo(
            PointBalanceLedger(
                payer = "UNILEVER",
                points = -100,
                transactionDate = result[0].transactionDate
            )
        )

        assertThat(result[1]).isEqualTo(
            PointBalanceLedger(
                payer = "DANNON",
                points = -400,
                transactionDate = result[1].transactionDate
            )
        )
    }

    @Test
    fun `deductPoints will throw an error if it tries to reduce a users points below 0`() {
        val dannonAddition = PointBalanceLedger("dannon", 400, Timestamp.valueOf("2020-02-11 12:00:00"))
        val unileverAddition = PointBalanceLedger("unilever", 300, Timestamp.valueOf("2021-01-11 12:00:00"))
        val dannonSecondAddition = PointBalanceLedger("dannon", 500, Timestamp.valueOf("2021-02-11 12:00:00"))

        subject.addPoints(listOf(dannonAddition, unileverAddition, dannonSecondAddition))

        try {
            subject.deductPoints(5000)
        } catch (ex: PointsNotUpdatedException) {
            val result = subject.find()

            assertThat(result.pointBalance).containsExactly(
                User.PointBalance("DANNON", 900),
                User.PointBalance("UNILEVER", 300)
            )
        }
    }
}