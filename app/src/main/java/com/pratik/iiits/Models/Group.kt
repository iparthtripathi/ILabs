package com.pratik.iiits.Models

data class Group(
    val id: String = "",
    val name: String = "",
    val admin: String = "",
    val members: List<String> = listOf()
)
