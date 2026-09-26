package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FruitCatalogData
import com.example.data.model.toPersianPrice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("خان بابایی", appName)
    }

    @Test
    fun `fruit catalog has items with valid prices`() {
        val items = FruitCatalogData.items
        assertTrue(items.isNotEmpty())
        items.forEach { item ->
            assertTrue(item.price > 0)
            assertTrue(item.title.isNotBlank())
            assertTrue(item.price.toPersianPrice().contains("تومان"))
        }
    }
}
