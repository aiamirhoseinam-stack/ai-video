package com.example.data.repository

import com.example.data.FruitCatalogData
import com.example.data.local.CartDao
import com.example.data.local.CartItemEntity
import com.example.data.local.OrderDao
import com.example.data.local.OrderEntity
import com.example.data.model.FruitCategory
import com.example.data.model.FruitItem
import kotlinx.coroutines.flow.Flow
import java.util.Random

data class StoreContactInfo(
    val name: String = "خان بابایی (میوه و تره‌بار)",
    val slogan: String = "عرضه میوه کیلویی دستچین، سفارشات مجلسی و پک‌های VIP",
    val address: String = "تهران، محله بریانک، کمیل، نرسیده به خیابان سلمان فارسی، پلاک ۱۱۶",
    val phone: String = "02155713393",
    val phoneDisplay: String = "۰۲۱-۵۵۷۱۳۳۹۳",
    val mapUrl: String = "https://maps.app.goo.gl/hwvRSLWum52cnK8CA",
    val workingHours: String = "همه روزه از ۸:۰۰ صبح تا ۲۳:۳۰ شب (حتی ایام تعطیل)",
    val deliveryInfo: String = "ارسال فوری پیک در محله بریانک و سراسر تهران | ارسال با خودروهای اختصاصی تشریفات",
    val bankCardNumber: String = "6219861805905584",
    val bankCardFormatted: String = "۶۲۱۹ - ۸۶۱۸ - ۰۵۹۰ - ۵۵۸۴",
    val bankName: String = "بانک سامان / شتاب",
    val bankAccountName: String = "خان بابایی",
    val bankSheba: String = "IR8405600000006219861805905584"
)

class FruitRepository(
    private val cartDao: CartDao,
    private val orderDao: OrderDao
) {
    val storeInfo = StoreContactInfo()

    fun getCatalog(): List<FruitItem> = FruitCatalogData.items

    fun getFeaturedItems(): List<FruitItem> =
        FruitCatalogData.items.filter { it.isDailyFeatured || it.isBestSeller }

    fun getVipAndCeremonialItems(): List<FruitItem> =
        FruitCatalogData.items.filter { it.category == FruitCategory.VIP_PACKS || it.category == FruitCategory.CEREMONIAL || it.category == FruitCategory.BASKET }

    fun getItemsByCategory(category: FruitCategory): List<FruitItem> {
        return if (category == FruitCategory.ALL) {
            FruitCatalogData.items
        } else {
            FruitCatalogData.items.filter { it.category == category }
        }
    }

    val cartItems: Flow<List<CartItemEntity>> = cartDao.getAllCartItems()
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()

    suspend fun addToCart(item: FruitItem, quantity: Double) {
        val entity = CartItemEntity(
            productId = item.id,
            title = item.title,
            unit = item.unit,
            price = item.price,
            quantity = quantity,
            emoji = item.emoji,
            imageRes = item.imageRes
        )
        cartDao.insertOrUpdate(entity)
    }

    suspend fun updateQuantity(productId: String, newQuantity: Double) {
        if (newQuantity <= 0) {
            cartDao.deleteByProductId(productId)
        } else {
            // Find existing
            val item = FruitCatalogData.items.find { it.id == productId }
            if (item != null) {
                cartDao.insertOrUpdate(
                    CartItemEntity(
                        productId = item.id,
                        title = item.title,
                        unit = item.unit,
                        price = item.price,
                        quantity = newQuantity,
                        emoji = item.emoji,
                        imageRes = item.imageRes
                    )
                )
            }
        }
    }

    suspend fun removeFromCart(productId: String) {
        cartDao.deleteByProductId(productId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    suspend fun placeOrder(
        customerName: String,
        customerPhone: String,
        address: String,
        deliveryOption: String,
        deliveryDateInfo: String,
        notes: String,
        totalPrice: Long,
        totalItemsCount: Int,
        paymentMethod: String = "کارت به کارت شتاب",
        paymentRef: String = ""
    ): OrderEntity {
        val codeNumber = (10000 + Random().nextInt(90000)).toString()
        val orderCode = "KB-$codeNumber"
        val order = OrderEntity(
            orderCode = orderCode,
            customerName = customerName,
            customerPhone = customerPhone,
            address = address,
            deliveryOption = deliveryOption,
            deliveryDateInfo = deliveryDateInfo,
            notes = notes,
            totalPrice = totalPrice,
            totalItemsCount = totalItemsCount,
            paymentMethod = paymentMethod,
            paymentRef = paymentRef,
            status = "در حال گلچین میوه‌ها و آماده‌سازی"
        )
        val id = orderDao.insertOrder(order)
        cartDao.clearCart()
        return order.copy(id = id)
    }
}
