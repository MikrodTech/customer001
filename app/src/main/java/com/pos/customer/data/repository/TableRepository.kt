package com.pos.customer.data.repository

import com.pos.customer.data.local.PreferencesManager
import com.pos.customer.data.local.TableDao
import com.pos.customer.data.model.Table
import com.pos.customer.data.model.TableStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableRepository @Inject constructor(
    private val tableDao: TableDao,
    private val preferencesManager: PreferencesManager
) {
    fun getAllTables(): Flow<List<Table>> = tableDao.getAllTables()

    fun getAvailableTables(): Flow<List<Table>> = tableDao.getAvailableTables()

    fun getAvailableTableCount(): Flow<Int> = tableDao.getAvailableTableCount()

    suspend fun getTableById(tableId: String): Table? = tableDao.getTableById(tableId)

    suspend fun selectTable(table: Table) {
        preferencesManager.saveSelectedTable(table)
    }

    fun getSelectedTable(): Flow<Table?> = preferencesManager.getSelectedTable()

    suspend fun clearSelectedTable() {
        preferencesManager.clearSelectedTable()
    }

    suspend fun seedTables() {
        val tables = listOf(
            Table(id = "t1", number = 1, status = TableStatus.AVAILABLE, capacity = 2),
            Table(id = "t2", number = 2, status = TableStatus.AVAILABLE, capacity = 2),
            Table(id = "t3", number = 3, status = TableStatus.OCCUPIED, capacity = 4),
            Table(id = "t4", number = 4, status = TableStatus.AVAILABLE, capacity = 4),
            Table(id = "t5", number = 5, status = TableStatus.AVAILABLE, capacity = 4),
            Table(id = "t6", number = 6, status = TableStatus.RESERVED, capacity = 6),
            Table(id = "t7", number = 7, status = TableStatus.AVAILABLE, capacity = 6),
            Table(id = "t8", number = 8, status = TableStatus.AVAILABLE, capacity = 8),
            Table(id = "t9", number = 9, status = TableStatus.OCCUPIED, capacity = 2),
            Table(id = "t10", number = 10, status = TableStatus.AVAILABLE, capacity = 4),
            Table(id = "t11", number = 11, status = TableStatus.AVAILABLE, capacity = 6),
            Table(id = "t12", number = 12, status = TableStatus.AVAILABLE, capacity = 8)
        )
        tableDao.insertAll(tables)
    }
}
