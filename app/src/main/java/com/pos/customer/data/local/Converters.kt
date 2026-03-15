package com.pos.customer.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pos.customer.data.model.CartItem
import com.pos.customer.data.model.OrderStatus
import com.pos.customer.data.model.PaymentMethod
import com.pos.customer.data.model.PaymentStatus
import java.util.Date

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromCartItemList(value: List<CartItem>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCartItemList(value: String): List<CartItem> {
        val listType = object : TypeToken<List<CartItem>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromOrderStatus(status: OrderStatus): String {
        return status.name
    }

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus {
        return OrderStatus.valueOf(value)
    }

    @TypeConverter
    fun fromPaymentMethod(method: PaymentMethod): String {
        return method.name
    }

    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod {
        return PaymentMethod.valueOf(value)
    }

    @TypeConverter
    fun fromPaymentStatus(status: PaymentStatus): String {
        return status.name
    }

    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus {
        return PaymentStatus.valueOf(value)
    }
}
