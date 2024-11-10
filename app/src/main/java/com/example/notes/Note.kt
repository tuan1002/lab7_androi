package com.example.notes

data class Note(
    var id: String = "",
    val title: String = "",
    val content: String = "",
    val createdAt: Long = 0L
)

