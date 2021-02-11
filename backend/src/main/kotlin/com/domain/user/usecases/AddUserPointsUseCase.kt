package com.domain.user.usecases

import com.domain.user.PointBalanceLedger
import com.domain.user.ports.UserRepository
import org.springframework.stereotype.Component

@Component
class AddUserPointsUseCase(
    private val userRepository: UserRepository
) {
    fun execute(pointBalanceLedger: List<PointBalanceLedger>) = userRepository.addPoints(pointBalanceLedger)
}