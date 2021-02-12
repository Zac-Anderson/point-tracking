package com.domain.user.ports

import com.domain.user.PointBalanceLedger
import com.domain.user.User

interface UserRepository {
    fun find(): User
    fun addPoints(pointBalanceLedger: List<PointBalanceLedger>)
    fun deductPoints(points: Int): List<PointBalanceLedger>
}