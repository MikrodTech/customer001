package com.pos.customer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.customer.data.model.Table
import com.pos.customer.data.repository.TableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableSelectionViewModel @Inject constructor(
    private val tableRepository: TableRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TableSelectionUiState())
    val uiState: StateFlow<TableSelectionUiState> = _uiState.asStateFlow()

    init {
        loadTables()
    }

    private fun loadTables() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Seed mock data
            tableRepository.seedTables()
            
            tableRepository.getAllTables()
                .onEach { tables ->
                    val availableCount = tables.count { it.status == com.pos.customer.data.model.TableStatus.AVAILABLE }
                    _uiState.value = _uiState.value.copy(
                        tables = tables,
                        availableCount = availableCount,
                        isLoading = false
                    )
                }
                .launchIn(viewModelScope)
        }
    }

    fun selectTable(table: Table) {
        viewModelScope.launch {
            tableRepository.selectTable(table)
            _uiState.value = _uiState.value.copy(selectedTable = table)
        }
    }

    data class TableSelectionUiState(
        val tables: List<Table> = emptyList(),
        val selectedTable: Table? = null,
        val availableCount: Int = 0,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
