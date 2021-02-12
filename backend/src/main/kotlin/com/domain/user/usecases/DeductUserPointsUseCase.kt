package com.domain.user.usecases

import com.domain.user.ports.UserRepository
import org.springframework.stereotype.Component

@Component
class DeductUserPointsUseCase(
    private val userRepository: UserRepository
) {
    fun execute(points: Int) = userRepository.deductPoints(points)
}