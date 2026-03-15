package com.pos.customer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Offer(
    val id: String,
    val title: String,
    val description: String,
    val discount: Int,
    val imageUrl: String,
    val validUntil: Date,
    val code: String
) : Parcelable
