package com.example.flickrimagessearchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.flickrimagessearchapp.ui.theme.FlickrImagesSearchAppTheme

class DetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = intent.getStringExtra("title") ?: ""
        val mediaUrl = intent.getStringExtra("mediaUrl") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val published = intent.getStringExtra("published") ?: ""
        val author = intent.getStringExtra("author") ?: ""

        setContent {
                ImageDetailsScreen(title, mediaUrl, description, published, author)
        }
    }
}

@Composable
fun ImageDetailsScreen(title: String, mediaUrl: String, description: String, published: String, author: String) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Image(
            painter = rememberImagePainter(data = mediaUrl),
            contentDescription = title,
            modifier = Modifier.fillMaxWidth().height(240.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title, style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Author: $author", style = MaterialTheme.typography.body2)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Published: $published", style = MaterialTheme.typography.body2)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = description, style = MaterialTheme.typography.body1)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    FlickrImagesSearchAppTheme {
        ImageDetailsScreen("", "", "", "", "")
    }
}