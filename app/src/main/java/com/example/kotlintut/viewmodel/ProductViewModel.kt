package com.example.kotlintut.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlintut.data.db.DatabaseHelper
import com.example.kotlintut.data.model.Attribute
import com.example.kotlintut.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for the Product Browsing experience.
 */
data class ProductUiState(
    val categories: List<String> = listOf("Panini", "Primi", "Secondi", "Bevande"),
    val products: List<Product> = emptyList(),
    val selectedCategory: String? = null,
    val selectedProduct: Product? = null,
    val productAttributes: List<Attribute> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper = DatabaseHelper(application)

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category, isLoading = true) }
        viewModelScope.launch {
            try {
                val products = dbHelper.getProductsByCategory(category)
                _uiState.update { it.copy(products = products, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun selectProduct(product: Product) {
        _uiState.update { it.copy(selectedProduct = product, isLoading = true) }
        viewModelScope.launch {
            try {
                val attributes = dbHelper.getAttributesByProduct(product.name)
                _uiState.update { it.copy(productAttributes = attributes, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun clearProductSelection() {
        _uiState.update { it.copy(selectedProduct = null, productAttributes = emptyList()) }
    }

    fun clearCategorySelection() {
        _uiState.update { it.copy(selectedCategory = null, products = emptyList()) }
    }
}
