package com.example.kotlintut.data.model

import com.example.kotlintut.data.network.NetworkExtra
import com.example.kotlintut.data.network.NetworkIngredient

data class CartItem(
    val product: Product,
    val quantity: Int,
    val removedIngredients: List<NetworkIngredient> = emptyList(),
    val addedExtras: List<NetworkExtra> = emptyList()
) {
    fun getTotalPrice(): Double {
        val extrasTotal = addedExtras.sumOf { it.price }
        return (product.price + extrasTotal) * quantity
    }
}
