package com.pos.customer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.customer.data.model.Order
import com.pos.customer.data.model.OrderStatus
import com.pos.customer.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderStatusViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderStatusUiState())
    val uiState: StateFlow<OrderStatusUiState> = _uiState.asStateFlow()

    private var pollingJob: kotlinx.coroutines.Job? = null

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val order = orderRepository.getOrderById(orderId)
            if (order != null) {
                _uiState.value = _uiState.value.copy(
                    order = order,
                    isLoading = false
                )
                startPolling(orderId)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Order not found"
                )
            }
        }
    }

    fun observeCurrentOrder(tableId: String) {
        orderRepository.getCurrentOrderForTable(tableId)
            .onEach { order ->
                order?.let {
                    _uiState.value = _uiState.value.copy(order = it)
                    
                    // If order is served, stop polling
                    if (it.status == OrderStatus.SERVED) {
                        stopPolling()
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun startPolling(orderId: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(10000) // Poll every 10 seconds
                
                val order = orderRepository.getOrderById(orderId)
                order?.let {
                    _uiState.value = _uiState.value.copy(order = it)
                    
                    // Stop polling if order is complete
                    if (it.status == OrderStatus.SERVED || it.status == OrderStatus.CANCELLED) {
                        stopPolling()
                        return@launch
                    }
                }
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    fun refreshStatus() {
        _uiState.value.order?.let { order ->
            loadOrder(order.id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }

    data class OrderStatusUiState(
        val order: Order? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
