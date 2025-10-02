package com.example.onedaybetter.data

data class Goal(
    val id: Int,
    val name: String,
    val description: String,
    val targetDate: String,
    val targetValue: String = "+85"
)