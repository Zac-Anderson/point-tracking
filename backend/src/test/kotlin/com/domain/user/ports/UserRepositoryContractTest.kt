package com.domain.user.ports

import com.domain.user.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

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
}