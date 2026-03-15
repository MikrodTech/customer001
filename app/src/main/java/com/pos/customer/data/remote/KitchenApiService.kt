package com.pos.customer.data.remote

import com.pos.customer.data.model.MenuItem
import com.pos.customer.data.model.Order
import com.pos.customer.data.model.OrderStatus
import com.pos.customer.data.model.Table
import com.pos.customer.data.model.TableStatus
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * API Service for Kitchen Interface Integration
 * This interface defines the contract for communication between
 * Customer Interface and Kitchen Interface
 */
interface KitchenApiService {
    
    // Tables
    @GET("api/tables")
    suspend fun getAllTables(): Response<List<Table>>

    @GET("api/tables/available")
    suspend fun getAvailableTables(): Response<List<Table>>

    @PUT("api/tables/{tableId}/status")
    suspend fun updateTableStatus(
        @Path("tableId") tableId: String,
        @Body status: TableStatus
    ): Response<Table>

    // Menu Items
    @GET("api/menu")
    suspend fun getAllMenuItems(): Response<List<MenuItem>>

    @GET("api/menu/category/{category}")
    suspend fun getMenuItemsByCategory(
        @Path("category") category: String
    ): Response<List<MenuItem>>

    // Orders
    @POST("api/orders")
    suspend fun createOrder(@Body order: Order): Response<Order>

    @GET("api/orders/{orderId}")
    suspend fun getOrder(@Path("orderId") orderId: String): Response<Order>

    @GET("api/orders/table/{tableId}/current")
    suspend fun getCurrentOrderForTable(
        @Path("tableId") tableId: String
    ): Response<Order?>

    @PUT("api/orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: String,
        @Body status: OrderStatus
    ): Response<Order>

    // Real-time updates via WebSocket would be implemented separately
    // This is for REST API calls
}
