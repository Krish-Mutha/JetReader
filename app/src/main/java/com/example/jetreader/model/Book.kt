package com.example.jetreader.model

data class Book(
    val items: List<Item>,
    val kind: String,
    val totalItems: Int
)