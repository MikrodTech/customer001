package com.pos.customer.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.pos.customer.data.local.Converters
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "orders")
@TypeConverters(Converters::class)
data class Order(
    @PrimaryKey
    val id: String,
    val tableId: String,
    val tableNumber: Int,
    val items: List<CartItem>,
    val status: OrderStatus,
    val subtotal: Double,
    val discount: Double,
    val tax: Double,
    val totalAmount: Double,
    val paymentMethod: PaymentMethod,
    val paymentStatus: PaymentStatus,
    val mpesaTransactionId: String? = null,
    val createdAt: Date = Date(),
    val estimatedTimeMinutes: Int = 25,
    val completedAt: Date? = null
) : Parcelable {
    val isActive: Boolean
        get() = status != OrderStatus.SERVED && status != OrderStatus.CANCELLED
}

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    SERVED,
    CANCELLED
}

enum class PaymentMethod {
    CARD,
    CASH,
    MPESA
}

enum class PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}
