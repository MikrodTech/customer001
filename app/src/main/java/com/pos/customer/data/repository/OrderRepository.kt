package com.pos.customer.data.repository

import com.pos.customer.data.local.OrderDao
import com.pos.customer.data.model.CartItem
import com.pos.customer.data.model.Order
import com.pos.customer.data.model.OrderStatus
import com.pos.customer.data.model.PaymentMethod
import com.pos.customer.data.model.PaymentStatus
import com.pos.customer.data.remote.MpesaApiService
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderDao: OrderDao,
    private val cartRepository: CartRepository,
    private val mpesaApiService: MpesaApiService
) {
    fun getAllOrders(): Flow<List<Order>> = orderDao.getAllOrders()

    fun getActiveOrders(): Flow<List<Order>> = orderDao.getActiveOrders()

    fun getCurrentOrderForTable(tableId: String): Flow<Order?> = 
        orderDao.getCurrentOrderForTable(tableId)

    fun getActiveOrderCount(): Flow<Int> = orderDao.getActiveOrderCount()

    suspend fun getOrderById(orderId: String): Order? = orderDao.getOrderById(orderId)

    suspend fun createOrder(
        tableId: String,
        tableNumber: Int,
        paymentMethod: PaymentMethod
    ): Result<Order> {
        return try {
            val cartItems = cartRepository.getCartItemsList()
            if (cartItems.isEmpty()) {
                return Result.failure(IllegalStateException("Cart is empty"))
            }

            val order = Order(
                id = "ORD-${UUID.randomUUID().toString().take(8).uppercase()}",
                tableId = tableId,
                tableNumber = tableNumber,
                items = cartItems,
                status = OrderStatus.PENDING,
                subtotal = cartRepository.subtotal,
                discount = cartRepository.discount,
                tax = cartRepository.tax,
                totalAmount = cartRepository.total,
                paymentMethod = paymentMethod,
                paymentStatus = if (paymentMethod == PaymentMethod.CASH) PaymentStatus.PENDING else PaymentStatus.PROCESSING,
                createdAt = Date(),
                estimatedTimeMinutes = calculateEstimatedTime(cartItems)
            )

            orderDao.insertOrder(order)
            cartRepository.clearCart()
            
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus) {
        orderDao.updateOrderStatus(orderId, status)
        
        // If order is served, mark as completed
        if (status == OrderStatus.SERVED) {
            val order = orderDao.getOrderById(orderId)
            order?.let {
                val updatedOrder = it.copy(
                    status = status,
                    completedAt = Date()
                )
                orderDao.updateOrder(updatedOrder)
            }
        }
    }

    suspend fun updatePaymentStatus(
        orderId: String, 
        status: PaymentStatus, 
        transactionId: String? = null
    ) {
        orderDao.updatePaymentStatus(orderId, status, transactionId)
    }

    /**
     * Initiate M-Pesa STK Push payment
     * This sends a prompt to the customer's phone
     */
    suspend fun initiateMpesaPayment(
        orderId: String,
        phoneNumber: String,
        amount: Double
    ): Result<String> {
        return try {
            // Format phone number (remove leading 0, add 254)
            val formattedPhone = formatPhoneNumber(phoneNumber)
            
            // TODO: Implement actual M-Pesa integration
            // For now, return success with mock checkout request ID
            val checkoutRequestId = "ws_CO_${System.currentTimeMillis()}"
            
            // In production, you would:
            // 1. Get OAuth token from Safaricom
            // 2. Generate password using business shortcode, passkey and timestamp
            // 3. Make STK push request
            // 4. Return checkout request ID for polling
            
            Result.success(checkoutRequestId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Query M-Pesa payment status
     */
    suspend fun queryMpesaPaymentStatus(checkoutRequestId: String): Result<PaymentStatus> {
        return try {
            // TODO: Implement actual status query
            // For demo, randomly return success
            val status = if (System.currentTimeMillis() % 2 == 0L) {
                PaymentStatus.COMPLETED
            } else {
                PaymentStatus.PROCESSING
            }
            Result.success(status)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun formatPhoneNumber(phone: String): String {
        return when {
            phone.startsWith("0") -> "254${phone.substring(1)}"
            phone.startsWith("+") -> phone.substring(1)
            phone.startsWith("254") -> phone
            else -> "254$phone"
        }
    }

    private fun calculateEstimatedTime(items: List<CartItem>): Int {
        // Base preparation time + time per item
        val baseTime = 15
        val timePerItem = 3
        return baseTime + (items.sumOf { it.quantity } * timePerItem)
    }

    // Mock offers
    val offers = listOf(
        com.pos.customer.data.model.Offer(
            id = "o1",
            title = "Happy Hour Special",
            description = "20% off all beverages from 4PM to 7PM",
            discount = 20,
            imageUrl = "https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?w=600&h=300&fit=crop",
            validUntil = Date(System.currentTimeMillis() + 86400000 * 365),
            code = "HAPPY20"
        ),
        com.pos.customer.data.model.Offer(
            id = "o2",
            title = "Weekend Brunch",
            description = "Free dessert with any main course on weekends",
            discount = 0,
            imageUrl = "https://images.unsplash.com/photo-1533089862017-5614ecb352ae?w=600&h=300&fit=crop",
            validUntil = Date(System.currentTimeMillis() + 86400000 * 365),
            code = "BRUNCH"
        ),
        com.pos.customer.data.model.Offer(
            id = "o3",
            title = "First Order Discount",
            description = "Get 15% off your first order",
            discount = 15,
            imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=600&h=300&fit=crop",
            validUntil = Date(System.currentTimeMillis() + 86400000 * 365),
            code = "FIRST15"
        )
    )
}
