package com.pratik.iiits.Models

data class Poll(
    val question: String = "",
    val options: List<String> = listOf(),
    val createdAt: Long = 0L,
    val user: UserModel? = null
)

