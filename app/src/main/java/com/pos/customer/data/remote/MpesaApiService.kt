package com.pos.customer.data.remote

import com.pos.customer.data.model.MpesaQueryRequest
import com.pos.customer.data.model.MpesaQueryResponse
import com.pos.customer.data.model.MpesaStkPushRequest
import com.pos.customer.data.model.MpesaStkPushResponse
import com.pos.customer.data.model.MpesaTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MpesaApiService {
    
    // Get OAuth Token
    @GET("oauth/v1/generate?grant_type=client_credentials")
    suspend fun getAccessToken(
        @Header("Authorization") auth: String
    ): Response<MpesaTokenResponse>

    // STK Push - Initiate payment
    @POST("mpesa/stkpush/v1/processrequest")
    suspend fun initiateStkPush(
        @Header("Authorization") token: String,
        @Body request: MpesaStkPushRequest
    ): Response<MpesaStkPushResponse>

    // Query STK Push status
    @POST("mpesa/stkpushquery/v1/query")
    suspend fun queryStkPushStatus(
        @Header("Authorization") token: String,
        @Body request: MpesaQueryRequest
    ): Response<MpesaQueryResponse>
}
