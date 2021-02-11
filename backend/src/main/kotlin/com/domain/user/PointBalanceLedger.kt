package com.domain.user

import java.util.*

data class PointBalanceLedger(
    val payer: String,
    val points: Int,
    val transactionDate: Date
)