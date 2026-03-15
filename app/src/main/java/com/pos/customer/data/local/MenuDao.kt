package com.pos.customer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pos.customer.data.model.CategoryType
import com.pos.customer.data.model.MenuItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {
    @Query("SELECT * FROM menu_items WHERE isAvailable = 1")
    fun getAllMenuItems(): Flow<List<MenuItem>>

    @Query("SELECT * FROM menu_items WHERE category = :category AND isAvailable = 1")
    fun getMenuItemsByCategory(category: CategoryType): Flow<List<MenuItem>>

    @Query("SELECT * FROM menu_items WHERE id = :id")
    suspend fun getMenuItemById(id: String): MenuItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(menuItems: List<MenuItem>)

    @Query("SELECT * FROM menu_items WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchMenuItems(query: String): Flow<List<MenuItem>>

    @Query("SELECT * FROM menu_items WHERE isPopular = 1 AND isAvailable = 1")
    fun getPopularItems(): Flow<List<MenuItem>>

    @Query("SELECT * FROM menu_items WHERE isNew = 1 AND isAvailable = 1")
    fun getNewItems(): Flow<List<MenuItem>>
}
