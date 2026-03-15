package com.pos.customer.data.repository

import com.pos.customer.data.model.CartItem
import com.pos.customer.data.model.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor() {
    
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _appliedOfferCode = MutableStateFlow<String?>(null)
    val appliedOfferCode: StateFlow<String?> = _appliedOfferCode.asStateFlow()

    val itemCount: Int
        get() = _cartItems.value.sumOf { it.quantity }

    val subtotal: Double
        get() = _cartItems.value.sumOf { it.totalPrice }

    val discount: Double
        get() {
            val code = _appliedOfferCode.value ?: return 0.0
            return when (code) {
                "HAPPY20" -> subtotal * 0.20
                "FIRST15" -> subtotal * 0.15
                else -> 0.0
            }
        }

    val tax: Double
        get() = (subtotal - discount) * TAX_RATE

    val total: Double
        get() = subtotal - discount + tax

    fun addToCart(menuItem: MenuItem) {
        _cartItems.update { currentItems ->
            val existingItem = currentItems.find { it.menuItem.id == menuItem.id }
            if (existingItem != null) {
                currentItems.map {
                    if (it.menuItem.id == menuItem.id) {
                        it.copy(quantity = it.quantity + 1)
                    } else it
                }
            } else {
                currentItems + CartItem(menuItem = menuItem)
            }
        }
    }

    fun removeFromCart(itemId: String) {
        _cartItems.update { currentItems ->
            currentItems.filter { it.menuItem.id != itemId }
        }
    }

    fun updateQuantity(itemId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(itemId)
            return
        }
        _cartItems.update { currentItems ->
            currentItems.map {
                if (it.menuItem.id == itemId) {
                    it.copy(quantity = quantity)
                } else it
            }
        }
    }

    fun updateSpecialInstructions(itemId: String, instructions: String) {
        _cartItems.update { currentItems ->
            currentItems.map {
                if (it.menuItem.id == itemId) {
                    it.copy(specialInstructions = instructions)
                } else it
            }
        }
    }

    fun applyOffer(code: String) {
        _appliedOfferCode.value = code
    }

    fun removeOffer() {
        _appliedOfferCode.value = null
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _appliedOfferCode.value = null
    }

    fun getCartItemsList(): List<CartItem> = _cartItems.value.toList()

    companion object {
        private const val TAX_RATE = 0.08
    }
}
