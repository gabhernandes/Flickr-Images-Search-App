package com.example.flickrimagessearchapp

import com.example.flickrimagessearchapp.classes.FlickrImage
import io.mockk.coEvery
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.URL

@ExperimentalCoroutinesApi
class FetchImagesTest {

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(URL::class)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
        unmockkAll()
    }

    @Test
    fun fetchImages_returnsListOfFlickrImages() = runBlocking {
        // Mock the response from URL.readText()
        val jsonString = """
            {
                "items": [
                    {
                        "title": "Porcupine",
                        "link": "https://www.flickr.com/photos/photosbyblackwolf/53758695904/",
                        "media": {"m":"https://live.staticflickr.com/65535/53758695904_4954b2d59f_m.jpg"},
                        "date_taken": "2024-05-12T20:04:05-08:00",
                        "description": " <p><a href=\"https://www.flickr.com/people/photosbyblackwolf/\">Dan King Alaskan Photography</a> posted a photo:</p> <p><a href=\"https://www.flickr.com/photos/photosbyblackwolf/53758695904/\" title=\"Porcupine\"><img src=\"https://live.staticflickr.com/65535/53758695904_4954b2d59f_m.jpg\" width=\"240\" height=\"160\" alt=\"Porcupine\" /></a></p> <p>This spring, my wife and I have seen an abundance of porcupines. Usually, we may see one or two all year long, in the last week, we have seen 6 different porcupines. (One walked through our yard, of course, I was at work but my wife got a good video of it.) <br /> This porcupine was spotted near Wrangell St. Elias National Park. If it wasn't for this porcupine, and one other I photographed later in the day, we would not have seen any wildlife.</p> ",
                        "published": "2024-05-30T22:05:40Z",
                        "author": "nobody@flickr.com (\"Dan King Alaskan Photography\")"
                    }
                ]
            }
        """.trimIndent()

        // Ensure the URL class is properly mocked
        coEvery { URL(any()).readText() } returns jsonString

        val expectedImages = listOf(
            FlickrImage(
                title = "Porcupine",
                link = "https://www.flickr.com/photos/photosbyblackwolf/53758695904/",
                mediaUrl = "https://live.staticflickr.com/65535/53758695904_4954b2d59f_m.jpg",
                description = "This spring, my wife and I have seen an abundance of porcupines. Usually, we may see one or two all year long, in the last week, we have seen 6 different porcupines. (One walked through our yard, of course, I was at work but my wife got a good video of it.) <br /> This porcupine was spotted near Wrangell St. Elias National Park. If it wasn't for this porcupine, and one other I photographed later in the day, we would not have seen any wildlife.",
                published = "2024-05-30T22:05:40Z",
                author = "nobody@flickr.com (\"Dan King Alaskan Photography\")"
            )
        )

        val actualImages = fetchImages("porcupine")
        assertEquals(expectedImages, actualImages)
    }
}
