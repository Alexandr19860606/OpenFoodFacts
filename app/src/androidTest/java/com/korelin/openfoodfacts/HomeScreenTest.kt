package com.korelin.openfoodfacts

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.korelin.openfoodfacts.ui.screens.home.HomeScreen
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testHomeScreen_displaysCorrectly() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            HomeScreen(navController = navController)
        }

        // Проверяем, что заголовок отображается
        composeTestRule
            .onNodeWithText("Open Food Facts")
            .assertIsDisplayed()

        // Проверяем, что кнопка поиска есть
        composeTestRule
            .onNodeWithContentDescription("Поиск")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun testHomeScreen_showsLoadingState() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            HomeScreen(navController = navController)
        }

        // Здесь можно добавить тест для состояния загрузки
        // Но в реальном приложении нужно мокать ViewModel
    }

    @Test
    fun testHomeScreen_navigatesToSearchOnSearchClick() {
        // Тест для проверки навигации
        // Требует мока NavController
    }

    @Test
    fun testAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.korelin.openfoodfacts", appContext.packageName)
    }
}