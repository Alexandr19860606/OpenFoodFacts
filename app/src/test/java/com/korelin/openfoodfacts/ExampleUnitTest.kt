package com.korelin.openfoodfacts

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun barcodeValidation_isCorrect() {
        val validBarcode = "3017620422003"
        val invalidBarcode = "123"

        assertTrue(validBarcode.length == 13)
        assertTrue(validBarcode.all { it.isDigit() })
        assertFalse(invalidBarcode.length == 13)
    }

    @Test
    fun productNameFormatting_isCorrect() {
        val name = "Nutella"
        val brand = "Ferrero"

        val formatted = "$name - $brand"
        assertEquals("Nutella - Ferrero", formatted)
    }
}