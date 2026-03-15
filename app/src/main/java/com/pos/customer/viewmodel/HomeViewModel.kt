package com.pos.customer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.customer.data.model.Order
import com.pos.customer.data.model.Table
import com.pos.customer.data.repository.OrderRepository
import com.pos.customer.data.repository.TableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tableRepository: TableRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        combine(
            tableRepository.getSelectedTable(),
            orderRepository.getActiveOrders()
        ) { selectedTable, activeOrders ->
            // Get current order for selected table
            val currentOrder = selectedTable?.let { table ->
                activeOrders.find { it.tableId == table.id && it.isActive }
            }

            _uiState.value = _uiState.value.copy(
                selectedTable = selectedTable,
                currentOrder = currentOrder,
                hasActiveOrder = currentOrder != null,
                isLoading = false
            )
        }.launchIn(viewModelScope)
    }

    fun selectTable(table: Table) {
        viewModelScope.launch {
            tableRepository.selectTable(table)
        }
    }

    fun clearTableSelection() {
        viewModelScope.launch {
            tableRepository.clearSelectedTable()
        }
    }

    fun refreshOrderStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Trigger recomposition by reloading
            loadData()
        }
    }

    data class HomeUiState(
        val selectedTable: Table? = null,
        val currentOrder: Order? = null,
        val hasActiveOrder: Boolean = false,
        val isLoading: Boolean = true,
        val error: String? = null
    )
}
