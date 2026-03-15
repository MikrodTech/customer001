package com.pos.customer.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pos.customer.data.model.Table
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pos_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()

    companion object {
        val SELECTED_TABLE = stringPreferencesKey("selected_table")
        val CART_ITEMS = stringPreferencesKey("cart_items")
        val APPLIED_OFFER = stringPreferencesKey("applied_offer")
        val CUSTOMER_PHONE = stringPreferencesKey("customer_phone")
    }

    suspend fun saveSelectedTable(table: Table) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_TABLE] = gson.toJson(table)
        }
    }

    fun getSelectedTable(): Flow<Table?> {
        return context.dataStore.data.map { preferences ->
            preferences[SELECTED_TABLE]?.let {
                gson.fromJson(it, Table::class.java)
            }
        }
    }

    suspend fun clearSelectedTable() {
        context.dataStore.edit { preferences ->
            preferences.remove(SELECTED_TABLE)
        }
    }

    suspend fun saveCustomerPhone(phone: String) {
        context.dataStore.edit { preferences ->
            preferences[CUSTOMER_PHONE] = phone
        }
    }

    fun getCustomerPhone(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[CUSTOMER_PHONE]
        }
    }

    suspend fun saveAppliedOffer(offerCode: String) {
        context.dataStore.edit { preferences ->
            preferences[APPLIED_OFFER] = offerCode
        }
    }

    fun getAppliedOffer(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[APPLIED_OFFER]
        }
    }

    suspend fun clearAppliedOffer() {
        context.dataStore.edit { preferences ->
            preferences.remove(APPLIED_OFFER)
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
