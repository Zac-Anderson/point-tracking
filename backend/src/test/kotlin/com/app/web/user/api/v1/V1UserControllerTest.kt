package com.app.web.user.api.v1

import com.domain.user.User
import com.domain.user.usecases.GetUserUseCase
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
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
            .andExpect(MockMvcResultMatchers.content().json(
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
}