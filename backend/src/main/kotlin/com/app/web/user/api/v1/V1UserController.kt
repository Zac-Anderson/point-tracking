package com.app.web.user.api.v1

import com.domain.user.usecases.GetUserUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class V1UserController(
    private val getUserUseCase: GetUserUseCase
) {

    @GetMapping
    fun index(): ResponseEntity<V1UserResponse> {
        val user = getUserUseCase.execute()
        return ResponseEntity.ok(V1UserResponseTranslator.translate(user))
    }
}