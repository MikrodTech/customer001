package com.pos.customer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.customer.data.model.Order
import com.pos.customer.data.model.PaymentMethod
import com.pos.customer.data.model.PaymentStatus
import com.pos.customer.data.repository.CartRepository
import com.pos.customer.data.repository.OrderRepository
import com.pos.customer.data.repository.TableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val tableRepository: TableRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    val cartItems = cartRepository.cartItems
    val subtotal: Double get() = cartRepository.subtotal
    val discount: Double get() = cartRepository.discount
    val tax: Double get() = cartRepository.tax
    val total: Double get() = cartRepository.total

    fun selectPaymentMethod(method: PaymentMethod) {
        _uiState.value = _uiState.value.copy(selectedPaymentMethod = method)
    }

    fun updatePhoneNumber(phone: String) {
        _uiState.value = _uiState.value.copy(phoneNumber = phone)
    }

    fun updateCustomerInfo(name: String, email: String) {
        _uiState.value = _uiState.value.copy(
            customerName = name,
            customerEmail = email
        )
    }

    fun processPayment() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isProcessing = true,
                error = null
            )

            try {
                val selectedTable = tableRepository.getSelectedTable()
                    .collect { it }
                    .toString()
                    .let { tableRepository.getTableById(it) }
                    
                // For demo, get table from DataStore
                // In production, use proper flow collection
                
                val table = tableRepository.getSelectedTable()
                    .let { flow ->
                        var table: com.pos.customer.data.model.Table? = null
                        flow.collect { table = it }
                        table
                    }

                if (table == null) {
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        error = "No table selected"
                    )
                    return@launch
                }

                // Create order
                val result = orderRepository.createOrder(
                    tableId = table.id,
                    tableNumber = table.number,
                    paymentMethod = _uiState.value.selectedPaymentMethod
                )

                result.onSuccess { order ->
                    // If M-Pesa, initiate STK Push
                    if (_uiState.value.selectedPaymentMethod == PaymentMethod.MPESA) {
                        initiateMpesaPayment(order)
                    } else {
                        // Card or Cash - complete immediately
                        _uiState.value = _uiState.value.copy(
                            isProcessing = false,
                            order = order,
                            paymentComplete = true
                        )
                    }
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        error = error.message ?: "Failed to create order"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = e.message ?: "An error occurred"
                )
            }
        }
    }

    private suspend fun initiateMpesaPayment(order: Order) {
        val phoneNumber = _uiState.value.phoneNumber
        
        if (phoneNumber.isBlank()) {
            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                error = "Please enter your M-Pesa phone number"
            )
            return
        }

        // Initiate STK Push
        val result = orderRepository.initiateMpesaPayment(
            orderId = order.id,
            phoneNumber = phoneNumber,
            amount = order.totalAmount
        )

        result.onSuccess { checkoutRequestId ->
            _uiState.value = _uiState.value.copy(
                checkoutRequestId = checkoutRequestId,
                isWaitingForMpesa = true
            )
            
            // Poll for payment status
            pollMpesaStatus(order.id, checkoutRequestId)
        }.onFailure { error ->
            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                error = error.message ?: "Failed to initiate M-Pesa payment"
            )
        }
    }

    private fun pollMpesaStatus(orderId: String, checkoutRequestId: String) {
        viewModelScope.launch {
            var attempts = 0
            val maxAttempts = 30 // Poll for 5 minutes (10 seconds * 30)
            
            while (attempts < maxAttempts) {
                delay(10000) // Wait 10 seconds between polls
                
                val result = orderRepository.queryMpesaPaymentStatus(checkoutRequestId)
                
                result.onSuccess { status ->
                    when (status) {
                        PaymentStatus.COMPLETED -> {
                            orderRepository.updatePaymentStatus(orderId, status, checkoutRequestId)
                            _uiState.value = _uiState.value.copy(
                                isProcessing = false,
                                isWaitingForMpesa = false,
                                paymentComplete = true
                            )
                            return@launch
                        }
                        PaymentStatus.FAILED -> {
                            _uiState.value = _uiState.value.copy(
                                isProcessing = false,
                                isWaitingForMpesa = false,
                                error = "M-Pesa payment failed. Please try again."
                            )
                            return@launch
                        }
                        else -> {
                            // Still processing, continue polling
                        }
                    }
                }
                
                attempts++
            }
            
            // Timeout
            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                isWaitingForMpesa = false,
                error = "Payment confirmation timed out. Please check your M-Pesa messages."
            )
        }
    }

    fun retryPayment() {
        _uiState.value = _uiState.value.copy(
            error = null,
            isProcessing = false,
            isWaitingForMpesa = false
        )
    }

    fun reset() {
        _uiState.value = CheckoutUiState()
    }

    data class CheckoutUiState(
        val selectedPaymentMethod: PaymentMethod = PaymentMethod.MPESA,
        val phoneNumber: String = "",
        val customerName: String = "",
        val customerEmail: String = "",
        val isProcessing: Boolean = false,
        val isWaitingForMpesa: Boolean = false,
        val paymentComplete: Boolean = false,
        val checkoutRequestId: String? = null,
        val order: Order? = null,
        val error: String? = null
    )
}
