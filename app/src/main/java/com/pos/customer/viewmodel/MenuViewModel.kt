package com.pos.customer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.customer.data.model.CartItem
import com.pos.customer.data.model.CategoryType
import com.pos.customer.data.model.MenuItem
import com.pos.customer.data.repository.CartRepository
import com.pos.customer.data.repository.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    val cartItems: StateFlow<List<CartItem>> = cartRepository.cartItems
    val cartItemCount: Int get() = cartRepository.itemCount
    val cartTotal: Double get() = cartRepository.total

    init {
        loadMenuItems()
        observeCart()
    }

    private fun loadMenuItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Seed mock data if needed
            menuRepository.seedMenuItems()
            
            menuRepository.getAllMenuItems()
                .onEach { items ->
                    _uiState.value = _uiState.value.copy(
                        menuItems = items,
                        filteredItems = filterItems(items, _uiState.value.selectedCategory, _uiState.value.searchQuery),
                        isLoading = false
                    )
                }
                .launchIn(viewModelScope)
        }
    }

    private fun observeCart() {
        cartRepository.cartItems
            .onEach { items ->
                _uiState.value = _uiState.value.copy(
                    cartSummary = CartSummary(
                        itemCount = items.sumOf { it.quantity },
                        subtotal = cartRepository.subtotal,
                        discount = cartRepository.discount,
                        tax = cartRepository.tax,
                        total = cartRepository.total
                    )
                )
            }
            .launchIn(viewModelScope)
    }

    fun selectCategory(category: CategoryType) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            filteredItems = filterItems(_uiState.value.menuItems, category, _uiState.value.searchQuery)
        )
    }

    fun search(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredItems = filterItems(_uiState.value.menuItems, _uiState.value.selectedCategory, query)
        )
    }

    fun addToCart(menuItem: MenuItem) {
        cartRepository.addToCart(menuItem)
    }

    fun removeFromCart(itemId: String) {
        cartRepository.removeFromCart(itemId)
    }

    fun updateQuantity(itemId: String, quantity: Int) {
        cartRepository.updateQuantity(itemId, quantity)
    }

    fun updateSpecialInstructions(itemId: String, instructions: String) {
        cartRepository.updateSpecialInstructions(itemId, instructions)
    }

    fun clearCart() {
        cartRepository.clearCart()
    }

    private fun filterItems(
        items: List<MenuItem>,
        category: CategoryType,
        query: String
    ): List<MenuItem> {
        return items.filter { item ->
            val matchesCategory = item.category == category
            val matchesQuery = query.isBlank() || 
                item.name.contains(query, ignoreCase = true) ||
                item.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    data class MenuUiState(
        val menuItems: List<MenuItem> = emptyList(),
        val filteredItems: List<MenuItem> = emptyList(),
        val selectedCategory: CategoryType = CategoryType.STARTERS,
        val searchQuery: String = "",
        val categories: List<com.pos.customer.data.model.Category> = emptyList(),
        val cartSummary: CartSummary = CartSummary(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    data class CartSummary(
        val itemCount: Int = 0,
        val subtotal: Double = 0.0,
        val discount: Double = 0.0,
        val tax: Double = 0.0,
        val total: Double = 0.0
    )
}
