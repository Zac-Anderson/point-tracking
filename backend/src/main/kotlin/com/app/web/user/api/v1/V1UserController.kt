package com.app.web.user.api.v1

import com.app.web.user.api.v1.pointBalance.*
import com.domain.user.usecases.AddUserPointsUseCase
import com.domain.user.usecases.DeductUserPointsUseCase
import com.domain.user.usecases.GetUserUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class V1UserController(
    private val getUserUseCase: GetUserUseCase,
    private val addUserPointsUseCase: AddUserPointsUseCase,
    private val deductUserPointsUseCase: DeductUserPointsUseCase
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun index(): V1UserResponse {
        val user = getUserUseCase.execute()
        return V1UserResponseTranslator.translate(user)
    }

    @GetMapping("balance")
    @ResponseStatus(HttpStatus.OK)
    fun showBalance(): List<V1UserPointBalanceResponse> {
        val user = getUserUseCase.execute()
        return user.pointBalance.map(V1UserPointBalanceResponseTranslator::translate)
    }

    @PostMapping("add")
    @ResponseStatus(HttpStatus.CREATED)
    fun addPoints(@RequestBody request: List<V1UserPointBalanceRequest>) {
        val ledger = request.map(V1UserPointBalanceRequestTranslator::translate)
        addUserPointsUseCase.execute(ledger)
    }

    @PutMapping("deduct{points}")
    @ResponseStatus(HttpStatus.OK)
    fun deductPoints(@RequestParam points: Int): List<V1UserPointDeductionResponse> {
        val ledger = deductUserPointsUseCase.execute(points)
        return ledger.map(V1UserPointDeductionResponseTranslator::translate)
    }
}