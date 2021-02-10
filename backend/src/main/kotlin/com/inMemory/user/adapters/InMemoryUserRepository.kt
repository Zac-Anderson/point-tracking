package com.inMemory.user.adapters

import com.domain.user.User
import com.domain.user.ports.UserRepository
import com.inMemory.user.InMemoryUser
import com.inMemory.user.InMemoryUserTranslator
import com.inMemory.user.UserCache
import org.springframework.stereotype.Component

@Component
class InMemoryUserRepository(
    private val userCache: UserCache
) : UserRepository {

    override fun find(): User {
        return when (userCache.size) {
            0 -> {
                userCache["user"] = InMemoryUser(pointBalance = emptyList())
                InMemoryUserTranslator.translate(userCache["user"]!!)
            }
            else -> InMemoryUserTranslator.translate(userCache["user"]!!)
        }
    }
}
