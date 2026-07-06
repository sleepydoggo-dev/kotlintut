package com.example.kotlintut.data.model

import androidx.compose.runtime.Immutable
import com.example.kotlintut.data.network.NetworkOption

@Immutable
data class Product(
    val id: String = "",
    val name: String,
    val price: Double,
    val description: String,
    val imageKey: String,
    val category: String = "",
    val formats: List<NetworkOption> = emptyList(),
    val sizes: List<NetworkOption> = emptyList()
)
