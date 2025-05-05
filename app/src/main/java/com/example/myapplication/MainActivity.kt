package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Image
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DemoScreen()
            }
        }
    }
}

// Placeholder background
@Composable
fun DemoScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.yardcropped),
            contentDescription =  "Neko Atsume Yard for Placeholder",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.matchParentSize()
        )
    }
}

// For logging what birds you have seen
@Composable
fun LogBookScreen() {

}

// Recording setting adjustment
@Composable
fun BottomSheet( uiState  : UiState,
                 modifier : Modifier,
                 location : (value: String) -> Unit,
                 date     : (value: String) -> Unit ) {


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        DemoScreen()
    }
}
