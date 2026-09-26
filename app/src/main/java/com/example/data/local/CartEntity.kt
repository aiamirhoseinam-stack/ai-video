package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val title: String,
    val unit: String,
    val price: Long,
    val quantity: Double,
    val emoji: String,
    val imageRes: Int? = null
)

@Entity(tableName = "customer_orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderCode: String,
    val customerName: String,
    val customerPhone: String,
    val address: String,
    val deliveryOption: String, // فوری، رزرو مجلسی، تحویل حضوری
    val deliveryDateInfo: String,
    val notes: String,
    val totalPrice: Long,
    val totalItemsCount: Int,
    val paymentMethod: String = "کارت به کارت شتاب",
    val paymentRef: String = "",
    val status: String = "در حال گلچین و بسته‌بندی",
    val createdAt: Long = System.currentTimeMillis()
)
