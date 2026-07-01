package com.example.kotlintut.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.kotlintut.data.db.DatabaseHelper
import com.example.kotlintut.data.model.Attribute
import com.example.kotlintut.data.model.CartItem
import com.example.kotlintut.data.model.Order
import com.example.kotlintut.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI State for the Shopping Cart and Checkout.
 */
data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val isOrderConfirmed: Boolean = false
) {
    val total: Double get() = items.sumOf { it.getTotalPrice() }
}

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper = DatabaseHelper(application)

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun loadCart(username: String) {
        _uiState.update { it.copy(isLoading = true) }
        val items = dbHelper.loadCart(username)
        _uiState.update { it.copy(items = items, isLoading = false) }
    }

    fun addToCart(username: String?, product: Product, quantity: Int, attributes: List<Attribute>) {
        val currentItems = _uiState.value.items.toMutableList()
        val existingIndex = currentItems.indexOfFirst { 
            it.product.name == product.name && it.selectedAttributes == attributes 
        }

        if (existingIndex != -1) {
            val existingItem = currentItems[existingIndex]
            currentItems[existingIndex] = existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {
            currentItems.add(CartItem(product, quantity, attributes))
        }

        _uiState.update { it.copy(items = currentItems) }
        
        username?.let {
            dbHelper.saveCart(it, currentItems)
        }
    }

    fun updateQuantity(username: String?, item: CartItem, delta: Int) {
        val currentItems = _uiState.value.items.toMutableList()
        val index = currentItems.indexOf(item)
        if (index != -1) {
            val newQty = item.quantity + delta
            if (newQty > 0) {
                currentItems[index] = item.copy(quantity = newQty)
            } else {
                currentItems.removeAt(index)
            }
            _uiState.update { it.copy(items = currentItems) }
            username?.let { dbHelper.saveCart(it, currentItems) }
        }
    }

    fun removeItem(username: String?, item: CartItem) {
        val currentItems = _uiState.value.items.filter { it != item }
        _uiState.update { it.copy(items = currentItems) }
        username?.let { dbHelper.saveCart(it, currentItems) }
    }

    fun confirmOrder(username: String) {
        _uiState.update { it.copy(isLoading = true) }
        dbHelper.saveOrder(username, _uiState.value.total, _uiState.value.items)
        dbHelper.saveCart(username, emptyList())
        _uiState.update { it.copy(items = emptyList(), isLoading = false, isOrderConfirmed = true) }
    }

    fun loadOrders(username: String) {
        _uiState.update { it.copy(isLoading = true) }
        val orders = dbHelper.getOrdersByUser(username)
        _uiState.update { it.copy(orders = orders, isLoading = false) }
    }
    
    fun resetOrderConfirmation() {
        _uiState.update { it.copy(isOrderConfirmed = false) }
    }
    
    fun clearCart(username: String?) {
        _uiState.update { it.copy(items = emptyList()) }
        username?.let { dbHelper.saveCart(it, emptyList()) }
    }
}
