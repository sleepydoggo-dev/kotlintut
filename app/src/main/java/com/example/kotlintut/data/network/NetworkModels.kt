package com.example.kotlintut.data.network

import com.google.gson.annotations.SerializedName

/**
 * Wrapper generico per le risposte dell'API
 */
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<T>
)

/**
 * Modello categoria ricevuto dal server
 */
data class NetworkCategory(
    @SerializedName("_id") val id: String,
    @SerializedName("categoriaPadre") val parentCategory: com.google.gson.JsonElement?,
    @SerializedName("posizionamento") val position: Int,
    @SerializedName("categoria") val name: String,
    @SerializedName("visibile") val isVisible: Boolean
)

/**
 * Modello ingrediente base
 */
data class NetworkIngredient(
    @SerializedName("idIngrediente") val id: String,
    @SerializedName("nome") val name: String,
    @SerializedName("eliminabile") val isRemovable: String // "si" o "no"
)

/**
 * Modello aggiunta extra
 */
data class NetworkExtra(
    @SerializedName("idIngrediente") val id: String,
    @SerializedName("nome") val name: String,
    @SerializedName("prezzo") val price: Double
)

/**
 * Modello opzione singola (formato o dimensione)
 */
data class NetworkOption(
    @SerializedName("nome") val name: String,
    @SerializedName("prezzo") val price: Double
)

/**
 * Modello prodotto ricevuto dal server
 */
data class NetworkProduct(
    @SerializedName("_id") val id: String,
    @SerializedName("nome") val name: String,
    @SerializedName("prezzo") val price: Double,
    @SerializedName("categorie") val categories: List<String>,
    @SerializedName("immagine") val imageUrl: String,
    @SerializedName("disponibile") val isAvailable: Boolean,
    @SerializedName("ingredienti") val ingredients: List<NetworkIngredient>?,
    @SerializedName("aggiunte") val extras: List<NetworkExtra>?,
    @SerializedName("formato") val formats: List<NetworkOption>?,
    @SerializedName("dimensione") val sizes: List<NetworkOption>?
)
