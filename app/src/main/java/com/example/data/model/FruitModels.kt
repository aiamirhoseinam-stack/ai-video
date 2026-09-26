package com.example.data.model

import androidx.annotation.DrawableRes
import com.example.R
import java.text.DecimalFormat

enum class FruitCategory(val id: String, val title: String, val iconEmoji: String) {
    ALL("all", "همه اقلام", "✨"),
    KILO("kilo", "میوه کیلویی", "🍎"),
    CEREMONIAL("ceremonial", "سفارش مجلسی", "👑"),
    VIP_PACKS("vip_packs", "پک تشریفاتی VIP", "🎁"),
    VEGGIE("veggie", "تره‌بار و صیفی", "🥬"),
    BASKET("basket", "سبد هدیه لوکس", "🧺"),
    DRIED_FRUIT("dried_fruit", "خشکبار اعلا", "🌰")
}

data class FruitItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: FruitCategory,
    val price: Long, // in Tomans
    val originalPrice: Long? = null,
    val unit: String = "کیلوگرم",
    val stepIncrement: Double = 1.0,
    val defaultQuantity: Double = 1.0,
    val origin: String,
    val description: String,
    val tags: List<String> = emptyList(),
    val rating: Float = 4.9f,
    val reviewCount: Int = 120,
    val isAvailable: Boolean = true,
    val isVip: Boolean = false,
    val isBestSeller: Boolean = false,
    val isDailyFeatured: Boolean = false,
    @DrawableRes val imageRes: Int? = null,
    val emoji: String = "🍎"
)

fun Long.toPersianPrice(): String {
    val formatter = DecimalFormat("#,###")
    val formatted = formatter.format(this)
    return formatted.toPersianDigits() + " تومان"
}

fun String.toPersianDigits(): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val sb = StringBuilder()
    for (ch in this) {
        if (ch in '0'..'9') {
            sb.append(persianDigits[ch - '0'])
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}

fun Double.toPersianAmount(unit: String): String {
    val formatted = if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        String.format("%.1f", this)
    }
    return "${formatted.toPersianDigits()} $unit"
}
