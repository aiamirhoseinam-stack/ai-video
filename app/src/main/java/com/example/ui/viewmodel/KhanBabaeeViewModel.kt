package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FruitCatalogData
import com.example.data.local.AppDatabase
import com.example.data.local.CartItemEntity
import com.example.data.local.OrderEntity
import com.example.data.model.FruitCategory
import com.example.data.model.FruitItem
import com.example.data.repository.FruitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    HOME,
    CATALOG,
    VIP_SERVICES,
    CART,
    STORE_INFO
}

enum class SortType(val title: String) {
    POPULAR("پرفروش‌ترین"),
    CHEAPEST("ارزان‌ترین"),
    EXPENSIVE("گران‌ترین لوکس"),
    DAILY_SPECIAL("پیشنهاد ویژه روز")
}

class KhanBabaeeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = FruitRepository(database.cartDao(), database.orderDao())

    val storeInfo = repository.storeInfo

    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow(FruitCategory.ALL)
    val selectedCategory: StateFlow<FruitCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortType = MutableStateFlow(SortType.POPULAR)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()

    private val _selectedProduct = MutableStateFlow<FruitItem?>(null)
    val selectedProduct: StateFlow<FruitItem?> = _selectedProduct.asStateFlow()

    private val _showCheckoutDialog = MutableStateFlow(false)
    val showCheckoutDialog: StateFlow<Boolean> = _showCheckoutDialog.asStateFlow()

    private val _recentSubmittedOrder = MutableStateFlow<OrderEntity?>(null)
    val recentSubmittedOrder: StateFlow<OrderEntity?> = _recentSubmittedOrder.asStateFlow()

    // Cart Flow from Room
    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Orders Flow from Room
    val ordersHistory: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Total Cart Price
    val cartTotalPrice: StateFlow<Long> = cartItems.combine(cartItems) { items, _ ->
        items.sumOf { (it.price * it.quantity).toLong() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val cartTotalCount: StateFlow<Int> = cartItems.combine(cartItems) { items, _ ->
        items.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Filtered & Sorted Catalog
    val catalogItems: StateFlow<List<FruitItem>> = combine(
        _selectedCategory,
        _searchQuery,
        _sortType
    ) { category, query, sort ->
        var list = FruitCatalogData.items

        if (category != FruitCategory.ALL) {
            list = list.filter { it.category == category }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.subtitle.lowercase().contains(q) ||
                it.origin.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.tags.any { tag -> tag.lowercase().contains(q) }
            }
        }

        when (sort) {
            SortType.POPULAR -> list.sortedByDescending { it.reviewCount }
            SortType.CHEAPEST -> list.sortedBy { it.price }
            SortType.EXPENSIVE -> list.sortedByDescending { it.price }
            SortType.DAILY_SPECIAL -> list.sortedByDescending { it.isDailyFeatured }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FruitCatalogData.items)

    val featuredItems = repository.getFeaturedItems()
    val vipCeremonialItems = repository.getVipAndCeremonialItems()

    fun setTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    fun selectCategory(category: FruitCategory) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortType(sort: SortType) {
        _sortType.value = sort
    }

    fun openProductDetail(product: FruitItem) {
        _selectedProduct.value = product
    }

    fun closeProductDetail() {
        _selectedProduct.value = null
    }

    fun openCheckout() {
        _showCheckoutDialog.value = true
    }

    fun closeCheckout() {
        _showCheckoutDialog.value = false
    }

    fun dismissOrderSuccess() {
        _recentSubmittedOrder.value = null
    }

    fun addToCart(product: FruitItem, quantity: Double) {
        viewModelScope.launch {
            repository.addToCart(product, quantity)
        }
    }

    fun updateCartQuantity(productId: String, newQuantity: Double) {
        viewModelScope.launch {
            repository.updateQuantity(productId, newQuantity)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun submitOrder(
        customerName: String,
        customerPhone: String,
        address: String,
        deliveryOption: String,
        deliveryDateInfo: String,
        notes: String,
        paymentMethod: String = "کارت به کارت شتاب",
        paymentRef: String = ""
    ) {
        val total = cartTotalPrice.value
        val count = cartTotalCount.value
        viewModelScope.launch {
            val order = repository.placeOrder(
                customerName = customerName,
                customerPhone = customerPhone,
                address = address,
                deliveryOption = deliveryOption,
                deliveryDateInfo = deliveryDateInfo,
                notes = notes,
                totalPrice = total,
                totalItemsCount = count,
                paymentMethod = paymentMethod,
                paymentRef = paymentRef
            )
            _showCheckoutDialog.value = false
            _recentSubmittedOrder.value = order
        }
    }
}
