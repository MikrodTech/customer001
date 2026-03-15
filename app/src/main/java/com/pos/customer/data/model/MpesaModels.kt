package com.pos.customer.data.model

import com.google.gson.annotations.SerializedName

// M-Pesa STK Push Request
data class MpesaStkPushRequest(
    @SerializedName("BusinessShortCode")
    val businessShortCode: String,
    @SerializedName("Password")
    val password: String,
    @SerializedName("Timestamp")
    val timestamp: String,
    @SerializedName("TransactionType")
    val transactionType: String = "CustomerPayBillOnline",
    @SerializedName("Amount")
    val amount: Double,
    @SerializedName("PartyA")
    val partyA: String, // Customer phone number
    @SerializedName("PartyB")
    val partyB: String, // Business shortcode
    @SerializedName("PhoneNumber")
    val phoneNumber: String,
    @SerializedName("CallBackURL")
    val callBackURL: String,
    @SerializedName("AccountReference")
    val accountReference: String,
    @SerializedName("TransactionDesc")
    val transactionDesc: String
)

// M-Pesa STK Push Response
data class MpesaStkPushResponse(
    @SerializedName("MerchantRequestID")
    val merchantRequestId: String,
    @SerializedName("CheckoutRequestID")
    val checkoutRequestId: String,
    @SerializedName("ResponseCode")
    val responseCode: String,
    @SerializedName("ResponseDescription")
    val responseDescription: String,
    @SerializedName("CustomerMessage")
    val customerMessage: String?
)

// M-Pesa Query Request
data class MpesaQueryRequest(
    @SerializedName("BusinessShortCode")
    val businessShortCode: String,
    @SerializedName("Password")
    val password: String,
    @SerializedName("Timestamp")
    val timestamp: String,
    @SerializedName("CheckoutRequestID")
    val checkoutRequestId: String
)

// M-Pesa Query Response
data class MpesaQueryResponse(
    @SerializedName("ResponseCode")
    val responseCode: String,
    @SerializedName("ResponseDescription")
    val responseDescription: String,
    @SerializedName("MerchantRequestID")
    val merchantRequestId: String,
    @SerializedName("CheckoutRequestID")
    val checkoutRequestId: String,
    @SerializedName("ResultCode")
    val resultCode: String,
    @SerializedName("ResultDesc")
    val resultDesc: String
)

// M-Pesa Access Token Response
data class MpesaTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("expires_in")
    val expiresIn: String
)

// M-Pesa Callback Data
data class MpesaCallback(
    @SerializedName("Body")
    val body: CallbackBody
)

data class CallbackBody(
    @SerializedName("stkCallback")
    val stkCallback: StkCallback
)

data class StkCallback(
    @SerializedName("MerchantRequestID")
    val merchantRequestId: String,
    @SerializedName("CheckoutRequestID")
    val checkoutRequestId: String,
    @SerializedName("ResultCode")
    val resultCode: Int,
    @SerializedName("ResultDesc")
    val resultDesc: String,
    @SerializedName("CallbackMetadata")
    val callbackMetadata: CallbackMetadata?
)

data class CallbackMetadata(
    @SerializedName("Item")
    val items: List<CallbackItem>
)

data class CallbackItem(
    @SerializedName("Name")
    val name: String,
    @SerializedName("Value")
    val value: Any?
)
