package com.pos.customer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pos.customer.data.model.Table
import com.pos.customer.data.model.TableStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TableDao {
    @Query("SELECT * FROM tables")
    fun getAllTables(): Flow<List<Table>>

    @Query("SELECT * FROM tables WHERE status = 'AVAILABLE'")
    fun getAvailableTables(): Flow<List<Table>>

    @Query("SELECT * FROM tables WHERE id = :tableId")
    suspend fun getTableById(tableId: String): Table?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tables: List<Table>)

    @Update
    suspend fun updateTable(table: Table)

    @Query("UPDATE tables SET status = :status WHERE id = :tableId")
    suspend fun updateTableStatus(tableId: String, status: TableStatus)

    @Query("SELECT COUNT(*) FROM tables WHERE status = 'AVAILABLE'")
    fun getAvailableTableCount(): Flow<Int>
}
