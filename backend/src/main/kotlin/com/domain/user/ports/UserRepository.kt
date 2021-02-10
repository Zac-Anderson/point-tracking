package com.domain.user.ports

import com.domain.user.User

interface UserRepository {
    fun find(): User
}