package com.example.flickrimagessearchapp

import android.content.Intent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.flickrimagessearchapp.classes.FlickrImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testIntentCreation() {
        val mockImage = FlickrImage(
            title = "Test Image",
            link = "http://example.com",
            mediaUrl = "http://example.com/image.jpg",
            description = "Test Description",
            published = "2024-06-06T00:00:00Z",
            author = "Test Author"
        )

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.runOnUiThread {
                    activity.startActivity(
                        Intent(activity, DetailsActivity::class.java).apply {
                            putExtra("title", mockImage.title)
                            putExtra("mediaUrl", mockImage.mediaUrl)
                            putExtra("description", mockImage.description)
                            putExtra("published", mockImage.published)
                            putExtra("author", mockImage.author)
                        }
                    )
                }
            }
        }
    }
}