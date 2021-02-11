package com.app.web.user.api.v1

import com.app.web.user.api.v1.pointBalance.V1UserPointBalanceRequest
import com.app.web.user.api.v1.pointBalance.V1UserPointBalanceRequestTranslator
import com.domain.user.usecases.AddUserPointsUseCase
import com.domain.user.usecases.GetUserUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class V1UserController(
    private val getUserUseCase: GetUserUseCase,
    private val addUserPointsUseCase: AddUserPointsUseCase
) {

    @GetMapping
    fun index(): ResponseEntity<V1UserResponse> {
        val user = getUserUseCase.execute()
        return ResponseEntity.ok(V1UserResponseTranslator.translate(user))
    }

    @PostMapping("add")
    @ResponseStatus(HttpStatus.CREATED)
    fun addPoints(@RequestBody request: List<V1UserPointBalanceRequest>) {
        val ledger = request.map(V1UserPointBalanceRequestTranslator::translate)
        addUserPointsUseCase.execute(ledger)
    }
}