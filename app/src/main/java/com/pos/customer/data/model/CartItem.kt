package com.pos.customer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val menuItem: MenuItem,
    var quantity: Int = 1,
    val specialInstructions: String = ""
) : Parcelable {
    val totalPrice: Double
        get() = menuItem.price * quantity
}
