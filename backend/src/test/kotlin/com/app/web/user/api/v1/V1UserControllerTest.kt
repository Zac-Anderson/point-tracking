package com.app.web.user.api.v1

import com.domain.user.PointBalanceLedger
import com.domain.user.User
import com.domain.user.usecases.AddUserPointsUseCase
import com.domain.user.usecases.DeductUserPointsUseCase
import com.domain.user.usecases.GetUserUseCase
import com.inMemory.PointsNotUpdatedException
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.sql.Timestamp

@SpringBootTest
@RunWith(SpringRunner::class)
class V1UserControllerTest {
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var context: WebApplicationContext

    @MockBean
    private lateinit var mockGetUserUseCase: GetUserUseCase

    @MockBean
    private lateinit var mockAddUserPointsUseCase: AddUserPointsUseCase

    @MockBean
    private lateinit var mockDeductUserPointsUseCase: DeductUserPointsUseCase

    @Before
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `index should return the user`() {
        val user = User(
            pointBalance = listOf(
                User.PointBalance(
                    payer = "DANNON",
                    points = 1000
                ),
                User.PointBalance(
                    payer = "UNILEVER",
                    points = 5000
                )
            )
        )

        whenever(mockGetUserUseCase.execute()).thenReturn(user)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.content().json(
                    """
                        {
                            "pointBalance": [
                                {
                                    "payer": "DANNON",
                                    "points": 1000
                                },
                                {
                                    "payer": "UNILEVER",
                                    "points": 5000
                                }
                            ]
                         }
                    """
                )
            )
    }

    @Test
    fun `showBalance should return the user's point balance`() {
        val user = User(
            pointBalance = listOf(
                User.PointBalance(
                    payer = "DANNON",
                    points = 1000
                ),
                User.PointBalance(
                    payer = "UNILEVER",
                    points = 5000
                )
            )
        )

        whenever(mockGetUserUseCase.execute()).thenReturn(user)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/balance"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.content().json(
                    """
                        [
                            {
                                "payer": "DANNON",
                                "points": 1000
                            },
                            {
                                "payer": "UNILEVER",
                                "points": 5000
                            }
                        ]
                    """
                )
            )
    }

    @Test
    fun `addPoints should return a 201 Created Code when points are successfully added`() {
        val json = """
                        [
                            {"payer": "TEST", "points": 100, "transaction_date": "2021-02-11T12:00:00"},
                            {"payer": "TEST", "points": -100, "transaction_date": "2021-02-11T13:00:00"}
                        ]
                    """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/user/add")
                .contentType(APPLICATION_JSON).content(json)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun `addPoints should return a Bad Request when points are aren't added successfully`() {
        whenever(mockAddUserPointsUseCase.execute(any())).thenAnswer {
            throw PointsNotUpdatedException("Unable to add points")
        }

        val json = """
                        [
                            {"payer": "TEST", "points": 100, "transaction_date": "2021-02-11T12:00:00"},
                            {"payer": "TEST", "points": -100, "transaction_date": "2021-02-11T13:00:00"}
                        ]
                    """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/user/add")
                .contentType(APPLICATION_JSON).content(json)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `deductPoints should return a list of payers and how much points were deducted from them`() {
        val now = Timestamp.valueOf("2021-02-11 12:00:00")
        val dannonDeduction = PointBalanceLedger("DANNON", -500, now)
        val unileverDeduction = PointBalanceLedger("UNILEVER", -500, now)

        whenever(mockDeductUserPointsUseCase.execute(any())).thenReturn(listOf(dannonDeduction, unileverDeduction))

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/deduct?points=1000"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.content().json(
                    """
                        [
                            {
                                "payer": "DANNON",
                                "points": -500,
                                "date": "2021-02-11T20:00:00.000+00:00"
                            },
                            {
                                "payer": "UNILEVER",
                                "points": -500,
                                "date": "2021-02-11T20:00:00.000+00:00"
                            }
                        ]
                    """
                )
            )
    }

    @Test
    fun `deductPoints should return a Bad Request when points are aren't deducted successfully`() {
        whenever(mockDeductUserPointsUseCase.execute(any())).thenAnswer {
            throw PointsNotUpdatedException("Unable to add points")
        }

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user/deduct?points=1000"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }
}