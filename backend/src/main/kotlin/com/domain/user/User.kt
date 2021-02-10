package com.domain.user

data class User(
    val pointBalance: List<PointBalance>
) {
    data class PointBalance(
        val payer: String,
        val points: Int
    )
}
