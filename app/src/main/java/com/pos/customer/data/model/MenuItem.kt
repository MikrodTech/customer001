package com.pos.customer.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "menu_items")
data class MenuItem(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: CategoryType,
    val imageUrl: String,
    val isAvailable: Boolean = true,
    val isPopular: Boolean = false,
    val isNew: Boolean = false,
    val discount: Int = 0
) : Parcelable

enum class CategoryType {
    STARTERS,
    MAIN_COURSE,
    DESSERTS,
    BEVERAGES
}

data class Category(
    val id: CategoryType,
    val name: String,
    val icon: String
)
