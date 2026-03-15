package com.pos.customer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pos.customer.data.model.Order
import com.pos.customer.data.model.OrderStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE status != 'SERVED' AND status != 'CANCELLED' ORDER BY createdAt DESC")
    fun getActiveOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): Order?

    @Query("SELECT * FROM orders WHERE tableId = :tableId AND status != 'SERVED' AND status != 'CANCELLED' ORDER BY createdAt DESC LIMIT 1")
    fun getCurrentOrderForTable(tableId: String): Flow<Order?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus)

    @Query("UPDATE orders SET paymentStatus = :status, mpesaTransactionId = :transactionId WHERE id = :orderId")
    suspend fun updatePaymentStatus(orderId: String, status: com.pos.customer.data.model.PaymentStatus, transactionId: String?)

    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrder(orderId: String)

    @Query("SELECT COUNT(*) FROM orders WHERE status != 'SERVED' AND status != 'CANCELLED'")
    fun getActiveOrderCount(): Flow<Int>
}
