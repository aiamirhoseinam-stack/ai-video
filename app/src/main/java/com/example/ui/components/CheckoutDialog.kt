package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.toPersianDigits
import com.example.data.model.toPersianPrice
import com.example.ui.theme.KhanGoldDark
import com.example.ui.theme.KhanGoldPrimary
import com.example.ui.theme.KhanGreenDark
import com.example.ui.theme.KhanGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutDialog(
    totalPrice: Long,
    totalItemsCount: Int,
    bankCardFormatted: String = "۶۲۱۹ - ۸۶۱۸ - ۰۵۹۰ - ۵۵۸۴",
    bankCardNumber: String = "6219861805905584",
    bankName: String = "بانک سامان / شتاب",
    bankAccountName: String = "خان بابایی",
    bankSheba: String = "IR8405600000006219861805905584",
    onDismiss: () -> Unit,
    onSubmit: (
        name: String,
        phone: String,
        address: String,
        deliveryOption: String,
        dateInfo: String,
        notes: String,
        paymentMethod: String,
        paymentRef: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("تهران، محله بریانک، ") }
    var selectedDelivery by remember { mutableStateOf("پیک فوری بریانک و تهران") }
    var ceremonyDateInfo by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // Payment method state
    var selectedPaymentMethod by remember { mutableStateOf("کارت به کارت شتاب") }
    var paymentReference by remember { mutableStateOf("") }
    var isCardCopied by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val deliveryOptions = listOf(
        Triple("پیک فوری بریانک و تهران", "ارسال سریع در زیر ۴۵ دقیقه", Icons.Default.DeliveryDining),
        Triple("سفارش مجلسی با هماهنگی زمان", "تحویل در موعد و تاریخ دقیق مراسم", Icons.Default.Event),
        Triple("تحویل حضوری در فروشگاه", "خیابان کمیل، نرسیده به سلمان فارسی، پلاک ۱۱۶", Icons.Default.Store)
    )

    val paymentMethods = listOf(
        Triple("کارت به کارت شتاب", "انتقال وجه به شماره کارت فروشگاه خان بابایی", Icons.Default.CreditCard),
        Triple("پرداخت آنلاین شاپرک", "پرداخت اینترنتی آنی با تمامی کارت‌های بانکی", Icons.Default.Language),
        Triple("پرداخت در محل با کارتخوان", "دستگاه پوز سیار پیک هنگام تحویل سفارش", Icons.Default.PointOfSale)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("checkout_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ثبت نهایی و پرداخت سفارش",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "فروشگاه میوه و تره‌بار خان بابایی (بریانک)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "بستن")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inputs
            OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it },
                label = { Text("نام و نام خانوادگی شما") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = KhanGreenPrimary)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_name_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KhanGreenPrimary,
                    focusedLabelColor = KhanGreenPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = customerPhone,
                onValueChange = { customerPhone = it },
                label = { Text("شماره همراه جهت هماهنگی پیک") },
                placeholder = { Text("مثال: ۰۹۱۲۳۴۵۶۷۸۹") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = KhanGreenPrimary)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_phone_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KhanGreenPrimary,
                    focusedLabelColor = KhanGreenPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Delivery Method Header
            Text(
                text = "۱. نحوه تحویل سفارش:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            deliveryOptions.forEach { (option, desc, icon) ->
                val isSelected = selectedDelivery == option
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { selectedDelivery = option },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedDelivery = option },
                            colors = RadioButtonDefaults.colors(selectedColor = KhanGreenPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) KhanGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = option,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = desc,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Ceremony time details if selected
            if (selectedDelivery.contains("مجلسی")) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ceremonyDateInfo,
                    onValueChange = { ceremonyDateInfo = it },
                    label = { Text("تاریخ و ساعت دقیق شروع مراسم و پذیرایی") },
                    placeholder = { Text("مثال: پنج‌شنبه ساعت ۱۷:۰۰ عصر") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Event, contentDescription = null, tint = KhanGoldPrimary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Address if not store pickup
            if (!selectedDelivery.contains("حضوری")) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("آدرس دقیق تحویل در تهران") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = KhanGreenPrimary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_address_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KhanGreenPrimary,
                        focusedLabelColor = KhanGreenPrimary
                    ),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // PAYMENT METHODS SECTION (کارت به کارت و خرید آنلاین)
            Text(
                text = "۲. انتخاب روش پرداخت:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            paymentMethods.forEach { (method, desc, icon) ->
                val isSelected = selectedPaymentMethod == method
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { selectedPaymentMethod = method },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) KhanGoldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, KhanGoldPrimary) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedPaymentMethod = method },
                            colors = RadioButtonDefaults.colors(selectedColor = KhanGoldDark)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) KhanGoldDark else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = method,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = desc,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // CARD-TO-CARD DETAILS COMPONENT (کارت به کارت)
            if (selectedPaymentMethod == "کارت به کارت شتاب") {
                Spacer(modifier = Modifier.height(10.dp))

                // Official Iranian Bank Card Visual
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    KhanGreenDark,
                                    KhanGreenPrimary,
                                    KhanGoldDark
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = bankName,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "حساب تجاری معتبر",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Card Number Large
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = bankCardFormatted,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "بنام: $bankAccountName",
                                    color = Color.White.copy(alpha = 0.95f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "شبا: $bankSheba",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 10.sp
                                )
                            }

                            // Copy Card Button
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("شماره کارت خان بابایی", bankCardNumber)
                                    clipboard.setPrimaryClip(clip)
                                    isCardCopied = true
                                    Toast.makeText(context, "شماره کارت کپی شد: $bankCardFormatted", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = KhanGoldPrimary),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCardCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isCardCopied) "کپی شد" else "کپی کارت",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Payment Reference Input Field
                OutlinedTextField(
                    value = paymentReference,
                    onValueChange = { paymentReference = it },
                    label = { Text("کد پیگیری واریز یا ۴ رقم آخر کارت شما") },
                    placeholder = { Text("مثال: ۱۲۳۴۵۶ یا ۹۸۷۶") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = KhanGoldDark)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_payment_ref_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KhanGoldDark,
                        focusedLabelColor = KhanGoldDark
                    ),
                    singleLine = true
                )
            } else if (selectedPaymentMethod == "پرداخت آنلاین شاپرک") {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = KhanGreenPrimary.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KhanGreenPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = KhanGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "پس از ثبت، به درگاه امن شاپرک متصل خواهید شد و رسید پرداخت به همراه پیامک ارسال می‌شود.",
                            fontSize = 11.sp,
                            lineHeight = 17.sp,
                            color = KhanGreenDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("توضیحات و ترجیح چیدمان میوه‌ها (اختیاری)") },
                placeholder = { Text("مثال: موزها کاملاً سفت باشند، انار درشت انتخاب شود...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 2
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cost Summary Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "مجموع اقلام (${totalItemsCount.toString().toPersianDigits()} قلم):",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = totalPrice.toPersianPrice(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "روش پرداخت:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedPaymentMethod,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = KhanGoldDark
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "هزینه ارسال پیک خان بابایی:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (selectedDelivery.contains("حضوری")) "رایگان (تحویل حضوری)" else "رایگان (سفارش ویژه)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = KhanGreenDark
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مبلغ نهایی قابل پرداخت:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = totalPrice.toPersianPrice(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = KhanGreenPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Final Submit Button
            Button(
                onClick = {
                    if (customerName.isBlank()) {
                        errorMessage = "لطفاً نام و نام خانوادگی خود را وارد کنید."
                        return@Button
                    }
                    if (customerPhone.isBlank() || customerPhone.length < 10) {
                        errorMessage = "لطفاً شماره تماس معتبر وارد فرمایید."
                        return@Button
                    }
                    if (!selectedDelivery.contains("حضوری") && address.trim().length <= 15) {
                        errorMessage = "لطفاً آدرس دقیق برای ارسال پیک را تکمیل نمایید."
                        return@Button
                    }
                    if (selectedPaymentMethod == "کارت به کارت شتاب" && paymentReference.isBlank()) {
                        errorMessage = "لطفاً کد پیگیری یا ۴ رقم آخر کارت واریزکننده را وارد فرمایید."
                        return@Button
                    }
                    errorMessage = null
                    onSubmit(
                        customerName,
                        customerPhone,
                        address,
                        selectedDelivery,
                        ceremonyDateInfo,
                        notes,
                        selectedPaymentMethod,
                        paymentReference
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_order_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KhanGreenPrimary)
            ) {
                Text(
                    text = if (selectedPaymentMethod == "پرداخت آنلاین شاپرک") "اتصال به درگاه بانکی و پرداخت" else "تأیید نهایی و ارسال به خان بابایی",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
