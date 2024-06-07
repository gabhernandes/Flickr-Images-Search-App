package com.example.flickrimagessearchapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.flickrimagessearchapp.classes.FlickrImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.regex.Pattern

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlickrSearchApp { image ->
                val intent = Intent(this, DetailsActivity::class.java).apply {
                    putExtra("title", image.title)
                    putExtra("mediaUrl", image.mediaUrl)
                    putExtra("description", image.description)
                    putExtra("published", image.published)
                    putExtra("author", image.author)
                }
                startActivity(intent)
            }
        }
    }
}

@Composable
fun FlickrSearchApp(onImageClick: (FlickrImage) -> Unit) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var images by remember { mutableStateOf(listOf<FlickrImage>()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        BasicTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                coroutineScope.launch {
                    isLoading = true
                    images = fetchImages(searchText.text)
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth().background(Color.LightGray)
                ) {
                    if (searchText.text.isEmpty()) {
                        Text("Search...", color = Color.Gray, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
                    }
                    innerTextField()
                }
            }
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(images.size) { index ->
                val image = images[index]
                ImageCard(image = image, onClick = { onImageClick(image) })
            }
        }
    }
}

@Composable
fun ImageCard(image: FlickrImage, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = 4.dp
    ) {
        Row {
            Image(
                painter = rememberImagePainter(data = image.mediaUrl),
                contentDescription = image.title,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = image.title, style = MaterialTheme.typography.h6)
                Text(text = image.author, style = MaterialTheme.typography.body2)
            }
        }
    }
}

suspend fun fetchImages(tags: String): List<FlickrImage> {
    return withContext(Dispatchers.IO) {
        val urlString = "https://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1&tags=${tags}"
        val response = URL(urlString).readText()
        val json = JSONObject(response)
        val items = json.getJSONArray("items")

        List(items.length()) { index ->
            val item = items.getJSONObject(index)
            val descriptionText = extractDescription(item.getString("description"))
            FlickrImage(
                title = item.getString("title"),
                link = item.getString("link"),
                mediaUrl = item.getJSONObject("media").getString("m"),
                description = descriptionText,
                published = item.getString("published"),
                author = item.getString("author")
            )
        }
    }
}

fun extractDescription(descriptionHtml: String): String {
    val pattern = Pattern.compile("<p>(.*?)</p>", Pattern.DOTALL)
    val matcher = pattern.matcher(descriptionHtml)
    val paragraphs = mutableListOf<String>()
    while (matcher.find()) {
        matcher.group(1)?.let { paragraphs.add(it.trim()) }
    }
    return paragraphs.getOrNull(2) ?: "No Description" // Get the third <p> or return the original description
}