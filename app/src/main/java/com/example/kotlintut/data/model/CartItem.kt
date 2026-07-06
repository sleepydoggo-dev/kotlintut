package com.example.kotlintut.data.model

import com.example.kotlintut.data.network.NetworkExtra
import com.example.kotlintut.data.network.NetworkIngredient
import com.example.kotlintut.data.network.NetworkOption

data class CartItem(
    val product: Product,
    val quantity: Int,
    val removedIngredients: List<NetworkIngredient> = emptyList(),
    val addedExtras: List<NetworkExtra> = emptyList(),
    val selectedFormat: NetworkOption? = null,
    val selectedSize: NetworkOption? = null
) {
    fun getTotalPrice(): Double {
        val extrasTotal = addedExtras.sumOf { it.price }
        val formatExtra = selectedFormat?.price ?: 0.0
        val sizeExtra = selectedSize?.price ?: 0.0
        return (product.price + extrasTotal + formatExtra + sizeExtra) * quantity
    }
}
