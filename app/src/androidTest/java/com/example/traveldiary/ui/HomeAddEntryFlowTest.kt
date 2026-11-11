package com.example.traveldiary.ui

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.traveldiary.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class HomeAddEntryFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addEntryAndReturnToHome() {
        grantLocationPermissions()

        composeRule.onNodeWithContentDescription("Nueva entrada").performClick()
        composeRule.onNodeWithText("Nuevo Recuerdo").assertIsDisplayed()

        val fields = composeRule.onAllNodes(hasSetTextAction())
        fields[0].performTextInput("Atardecer en Kyoto")
        fields[1].performTextInput("Kioto")
        fields[2].performTextInput("Japón")

        composeRule.onNodeWithText("Guardar").performClick()

        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.onAllNodesWithText("Atardecer en Kyoto")
                .fetchSemanticsNodes().isNotEmpty() &&
                composeRule.onAllNodesWithText("Nuevo Recuerdo")
                    .fetchSemanticsNodes().isEmpty()
        }
        composeRule.onAllNodesWithText("Atardecer en Kyoto")[0].assertIsDisplayed()
    }

    private fun grantLocationPermissions() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val packageName = instrumentation.targetContext.packageName
        instrumentation.uiAutomation.apply {
            grantRuntimePermission(packageName, Manifest.permission.ACCESS_FINE_LOCATION)
            grantRuntimePermission(packageName, Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }
}
