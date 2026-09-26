package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.toPersianDigits
import com.example.ui.components.CheckoutDialog
import com.example.ui.components.OrderSuccessDialog
import com.example.ui.components.ProductDetailDialog
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.StoreInfoScreen
import com.example.ui.screens.VipServicesScreen
import com.example.ui.theme.KhanBabaeeTheme
import com.example.ui.theme.KhanGoldDark
import com.example.ui.theme.KhanGoldPrimary
import com.example.ui.theme.KhanGreenDark
import com.example.ui.theme.KhanGreenPrimary
import com.example.ui.theme.KhanRuby
import com.example.ui.viewmodel.KhanBabaeeViewModel
import com.example.ui.viewmodel.ScreenTab

class MainActivity : ComponentActivity() {
    private val viewModel: KhanBabaeeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KhanBabaeeTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    KhanBabaeeApp(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhanBabaeeApp(viewModel: KhanBabaeeViewModel) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val cartTotalCount by viewModel.cartTotalCount.collectAsStateWithLifecycle()
    val cartTotalPrice by viewModel.cartTotalPrice.collectAsStateWithLifecycle()
    val catalogItems by viewModel.catalogItems.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortType by viewModel.sortType.collectAsStateWithLifecycle()
    val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
    val showCheckoutDialog by viewModel.showCheckoutDialog.collectAsStateWithLifecycle()
    val recentSubmittedOrder by viewModel.recentSubmittedOrder.collectAsStateWithLifecycle()
    val ordersHistory by viewModel.ordersHistory.collectAsStateWithLifecycle()

    val cartItemIds = cartItems.map { it.productId }.toSet()

    // Handle back button for sub-tabs
    BackHandler(enabled = currentTab != ScreenTab.HOME) {
        viewModel.setTab(ScreenTab.HOME)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_khanbabaee_icon),
                                contentDescription = "لوگو خان بابایی",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "خان بابایی",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = KhanGreenPrimary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "بریانک - کمیل",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KhanGreenPrimary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "فروش میوه کیلویی و سفارشات مجلسی",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Quick Call Action
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:02155713393")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.testTag("topbar_phone_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "تماس با فروشگاه",
                            tint = KhanGreenPrimary
                        )
                    }

                    // Cart Icon with Badge
                    IconButton(
                        onClick = { viewModel.setTab(ScreenTab.CART) },
                        modifier = Modifier.testTag("topbar_cart_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                if (cartTotalCount > 0) {
                                    Badge(
                                        containerColor = KhanRuby,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = cartTotalCount.toString().toPersianDigits(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (currentTab == ScreenTab.CART) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                                contentDescription = "سبد خرید",
                                tint = if (currentTab == ScreenTab.CART) KhanGreenPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == ScreenTab.HOME,
                    onClick = { viewModel.setTab(ScreenTab.HOME) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "خانه"
                        )
                    },
                    label = { Text("خانه", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = KhanGreenPrimary,
                        selectedTextColor = KhanGreenPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_home")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.CATALOG,
                    onClick = { viewModel.setTab(ScreenTab.CATALOG) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.CATALOG) Icons.Filled.LocalMall else Icons.Outlined.LocalMall,
                            contentDescription = "قیمت روز و میوه‌ها"
                        )
                    },
                    label = { Text("میوه‌ها", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = KhanGreenPrimary,
                        selectedTextColor = KhanGreenPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_catalog")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.VIP_SERVICES,
                    onClick = { viewModel.setTab(ScreenTab.VIP_SERVICES) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.VIP_SERVICES) Icons.Filled.Celebration else Icons.Outlined.Celebration,
                            contentDescription = "سفارش مجلسی و VIP"
                        )
                    },
                    label = { Text("تشریفات VIP", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = KhanGoldPrimary,
                        selectedTextColor = KhanGoldDark,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_vip")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.CART,
                    onClick = { viewModel.setTab(ScreenTab.CART) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (cartTotalCount > 0) {
                                    Badge(containerColor = KhanRuby, contentColor = Color.White) {
                                        Text(text = cartTotalCount.toString().toPersianDigits(), fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (currentTab == ScreenTab.CART) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                                contentDescription = "سبد خرید"
                            )
                        }
                    },
                    label = { Text("سبد خرید", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = KhanGreenPrimary,
                        selectedTextColor = KhanGreenPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_cart")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.STORE_INFO,
                    onClick = { viewModel.setTab(ScreenTab.STORE_INFO) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.STORE_INFO) Icons.Filled.Info else Icons.Outlined.Info,
                            contentDescription = "آدرس و تماس"
                        )
                    },
                    label = { Text("فروشگاه", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = KhanGreenPrimary,
                        selectedTextColor = KhanGreenPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_store")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ScreenTab.HOME -> {
                    HomeScreen(
                        featuredItems = viewModel.featuredItems,
                        vipItems = viewModel.vipCeremonialItems,
                        cartItemIds = cartItemIds,
                        onProductClick = { viewModel.openProductDetail(it) },
                        onQuickAdd = { viewModel.addToCart(it, it.defaultQuantity) },
                        onNavigateTab = { viewModel.setTab(it) },
                        onSelectCategory = { viewModel.selectCategory(it) }
                    )
                }

                ScreenTab.CATALOG -> {
                    CatalogScreen(
                        items = catalogItems,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        sortType = sortType,
                        cartItemIds = cartItemIds,
                        onSelectCategory = { viewModel.selectCategory(it) },
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onSortChange = { viewModel.setSortType(it) },
                        onProductClick = { viewModel.openProductDetail(it) },
                        onQuickAdd = { viewModel.addToCart(it, it.defaultQuantity) }
                    )
                }

                ScreenTab.VIP_SERVICES -> {
                    VipServicesScreen(
                        vipItems = viewModel.vipCeremonialItems,
                        cartItemIds = cartItemIds,
                        onProductClick = { viewModel.openProductDetail(it) },
                        onQuickAdd = { viewModel.addToCart(it, it.defaultQuantity) }
                    )
                }

                ScreenTab.CART -> {
                    CartScreen(
                        cartItems = cartItems,
                        totalPrice = cartTotalPrice,
                        ordersHistory = ordersHistory,
                        onUpdateQuantity = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                        onRemoveItem = { id -> viewModel.removeFromCart(id) },
                        onClearCart = { viewModel.clearCart() },
                        onOpenCheckout = { viewModel.openCheckout() },
                        onNavigateTab = { viewModel.setTab(it) }
                    )
                }

                ScreenTab.STORE_INFO -> {
                    StoreInfoScreen(
                        storeInfo = viewModel.storeInfo
                    )
                }
            }
        }
    }

    // Product Detail Dialog
    selectedProduct?.let { product ->
        ProductDetailDialog(
            product = product,
            onDismiss = { viewModel.closeProductDetail() },
            onAddToCart = { qty -> viewModel.addToCart(product, qty) }
        )
    }

    // Checkout Sheet Dialog
    if (showCheckoutDialog) {
        CheckoutDialog(
            totalPrice = cartTotalPrice,
            totalItemsCount = cartTotalCount,
            bankCardFormatted = viewModel.storeInfo.bankCardFormatted,
            bankCardNumber = viewModel.storeInfo.bankCardNumber,
            bankName = viewModel.storeInfo.bankName,
            bankAccountName = viewModel.storeInfo.bankAccountName,
            bankSheba = viewModel.storeInfo.bankSheba,
            onDismiss = { viewModel.closeCheckout() },
            onSubmit = { name, phone, address, delivery, dateInfo, notes, paymentMethod, paymentRef ->
                viewModel.submitOrder(name, phone, address, delivery, dateInfo, notes, paymentMethod, paymentRef)
            }
        )
    }

    // Order Success Dialog
    recentSubmittedOrder?.let { order ->
        OrderSuccessDialog(
            order = order,
            onDismiss = { viewModel.dismissOrderSuccess() },
            onViewHistory = {
                viewModel.dismissOrderSuccess()
                viewModel.setTab(ScreenTab.CART)
            }
        )
    }
}
