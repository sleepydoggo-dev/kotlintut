package com.example.kotlintut.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlintut.data.model.Attribute
import com.example.kotlintut.data.model.Product
import com.example.kotlintut.ui.components.TotemTopBar

/**
 * Categories Screen - Displays the list of available food categories.
 */
@Composable
fun CategoriesScreen(
    categories: List<String>,
    onCategoryClick: (String) -> Unit,
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = { TotemTopBar(onMenuClick = onMenuClick) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ScreenTitle(text = "Cosa vuoi ordinare?")
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(8.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(category = category) { onCategoryClick(category) }
                }
            }
        }
    }
}

/**
 * Products Screen - Displays products for a specific category.
 */
@Composable
fun ProductsScreen(
    category: String,
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = { 
            TotemTopBar(
                title = category, 
                showMenu = false, 
                showBack = true, 
                onBackClick = onBack
            ) 
        }
    ) { padding ->
        if (products.isEmpty()) {
            EmptyState(message = "Nessun prodotto trovato in questa categoria.")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(padding).padding(8.dp)
            ) {
                items(products) { product ->
                    ProductCard(product = product) { onProductClick(product) }
                }
            }
        }
    }
}

/**
 * Product Detail Screen - Allows selection of attributes and quantity.
 */
@Composable
fun ProductDetailScreen(
    product: Product,
    attributes: List<Attribute>,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onAddToCart: (Int, List<Attribute>) -> Unit,
    onBack: () -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }
    val selectedAttributes = remember { mutableStateListOf<Attribute>() }

    Scaffold(
        topBar = {
            TotemTopBar(
                title = "Dettagli",
                showMenu = false,
                showBack = true,
                onBackClick = onBack
            )
        },
        bottomBar = {
            ProductPurchaseBar(
                basePrice = product.price,
                quantity = quantity,
                selectedAttributes = selectedAttributes,
                onQuantityIncrease = { quantity++ },
                onQuantityDecrease = { if (quantity > 1) quantity-- },
                onAddToCart = { onAddToCart(quantity, selectedAttributes.toList()) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            ProductHeaderImage(
                productName = product.name,
                isFavorite = isFavorite,
                onFavoriteToggle = onFavoriteToggle
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ProductInfo(name = product.name, description = product.description)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (attributes.isNotEmpty()) {
                AttributeList(
                    attributes = attributes,
                    selectedAttributes = selectedAttributes,
                    onAttributeToggle = { attr ->
                        if (selectedAttributes.contains(attr)) selectedAttributes.remove(attr)
                        else selectedAttributes.add(attr)
                    }
                )
            }
        }
    }
}

// --- Sub-Components ---

@Composable
private fun ScreenTitle(text: String) {
    Text(
        text = text,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(24.dp).fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun CategoryCard(category: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(8.dp).aspectRatio(1f).clickable { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(category.take(1), fontSize = 48.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Text(
                text = category,
                modifier = Modifier.padding(12.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(8.dp).aspectRatio(0.8f).clickable { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(product.name.take(1), fontSize = 40.sp, color = MaterialTheme.colorScheme.secondary)
            }
            Text(
                text = product.name,
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Text(
                text = "€ ${String.format("%.2f", product.price)}",
                modifier = Modifier.padding(bottom = 12.dp),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ProductPurchaseBar(
    basePrice: Double,
    quantity: Int,
    selectedAttributes: List<Attribute>,
    onQuantityIncrease: () -> Unit,
    onQuantityDecrease: () -> Unit,
    onAddToCart: () -> Unit
) {
    Surface(shadowElevation = 16.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onQuantityDecrease) {
                Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Text(quantity.toString(), fontSize = 20.sp, modifier = Modifier.padding(horizontal = 16.dp))
            IconButton(onClick = onQuantityIncrease) {
                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onAddToCart,
                modifier = Modifier.height(55.dp)
            ) {
                val totalPrice = (basePrice + selectedAttributes.sumOf { it.extraPrice }) * quantity
                Text("AGGIUNGI  € ${String.format("%.2f", totalPrice)}")
            }
        }
    }
}

@Composable
private fun ProductHeaderImage(
    productName: String,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(productName.take(1), fontSize = 80.sp, color = MaterialTheme.colorScheme.secondary)
        }
        IconButton(
            onClick = onFavoriteToggle,
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }
    }
}

@Composable
private fun ProductInfo(name: String, description: String) {
    Column {
        Text(name, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(description, color = Color.Gray, fontSize = 16.sp)
    }
}

@Composable
private fun AttributeList(
    attributes: List<Attribute>,
    selectedAttributes: List<Attribute>,
    onAttributeToggle: (Attribute) -> Unit
) {
    Column {
        Text("Personalizza il tuo ordine", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        attributes.forEach { attr ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().clickable { onAttributeToggle(attr) }
            ) {
                Checkbox(
                    checked = selectedAttributes.contains(attr),
                    onCheckedChange = { onAttributeToggle(attr) }
                )
                Text(attr.toString(), modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, textAlign = TextAlign.Center, color = Color.Gray)
    }
}
