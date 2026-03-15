package com.pos.customer.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tables")
data class Table(
    @PrimaryKey
    val id: String,
    val number: Int,
    val status: TableStatus,
    val capacity: Int
) : Parcelable

enum class TableStatus {
    AVAILABLE,
    OCCUPIED,
    RESERVED
}
