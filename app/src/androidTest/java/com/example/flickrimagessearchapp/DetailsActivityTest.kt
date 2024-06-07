package com.example.flickrimagessearchapp

import android.content.Intent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailsActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun imageDetailsScreenDisplaysCorrectData() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), DetailsActivity::class.java).apply {
                putExtra("title", "Test Title")
                putExtra("mediaUrl", "https://example.com/test.jpg")
                putExtra("description", "Test Description")
                putExtra("published", "2024-05-30T22:05:40Z")
                putExtra("author", "Test Author")
            }

        ActivityScenario.launch<DetailsActivity>(intent).use {
            composeTestRule.onNodeWithText("Test Title").assertExists()
            composeTestRule.onNodeWithText("Author: Test Author").assertExists()
            composeTestRule.onNodeWithText("Published: 2024-05-30T22:05:40Z").assertExists()
            composeTestRule.onNodeWithText("Test Description").assertExists()
        }
    }
}