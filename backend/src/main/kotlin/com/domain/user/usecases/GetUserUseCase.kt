package com.domain.user.usecases

import com.domain.user.ports.UserRepository
import org.springframework.stereotype.Component

@Component
class GetUserUseCase(
    private val userRepository: UserRepository
) {
    fun execute() = userRepository.find()
}