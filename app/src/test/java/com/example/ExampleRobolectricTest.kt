package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.InitialData
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
    fun `verify app name resource matches Madar Affiliate`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("مدار للعمولات", appName)
    }

    @Test
    fun `verify initial catalog products have valid margins`() {
        val products = InitialData.initialProducts
        assertTrue("Products list should not be empty", products.isNotEmpty())
        products.forEach { prod ->
            assertTrue("Affiliate price should be higher than wholesale cost", prod.affiliatePrice > prod.originalPrice)
            assertTrue("Commission must be positive", prod.commissionAmount > 0)
        }
    }
}
