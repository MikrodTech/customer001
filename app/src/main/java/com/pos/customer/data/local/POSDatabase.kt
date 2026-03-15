package com.pos.customer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pos.customer.data.model.MenuItem
import com.pos.customer.data.model.Order
import com.pos.customer.data.model.Table

@Database(
    entities = [
        MenuItem::class,
        Order::class,
        Table::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class POSDatabase : RoomDatabase() {
    abstract fun menuDao(): MenuDao
    abstract fun orderDao(): OrderDao
    abstract fun tableDao(): TableDao
}
