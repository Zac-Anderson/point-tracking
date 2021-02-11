package com.app.web.user.api.v1

import com.domain.user.User
import com.domain.user.usecases.AddUserPointsUseCase
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
    fun `addPoints should return a Bad Request when points are successfully added`() {
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
}