package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

private const val EXTRA_BIRD_LIST = "com.example.myapplication.bird_list"
class LogBookActivity : AppCompatActivity() {

    private val birds = listOf(
        BirdResource(R.drawable.bird1, "Rock Pigeon"),
        BirdResource(R.drawable.bird2, "Sparrow"),
        BirdResource(R.drawable.bird3, "Cardinal"),
        BirdResource(R.drawable.bird4, "Blue Jay"),
    )

    data class BirdResource(val imageResId : Int, val name : String)

    companion object {
        fun newIntent(packageContext: Context, birdList: ArrayList<String>): Intent {
            requireNotNull(packageContext) {"Context cannot be Null"}
            
//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         setContentView(R.layout.activity_log_book)

//         val scrollView = ScrollView(this).apply {
//             setPadding(256) // Add padding to prevent content from being hidden
//         }
//         val textView = TextView(this)
//         scrollView.addView(textView)
//         setContentView(scrollView) // Set the ScrollView as the content view

//         // Initialize the database
//         val database = AppDatabase.getInstance(applicationContext) // Use singleton instance

//         // Populate the database with sample data for testing if needed
// //        database.populateWithSampleData()

//         // Fetch and display bird data on a background thread
//         lifecycleScope.launch(Dispatchers.IO) { // Use Dispatchers.IO for background work
//             val birdDao = database.birdDao()
//             val birds = birdDao.getAllBirds()
//             android.util.Log.d("LogBookActivity", "Fetched birds: $birds")
//             withContext(Dispatchers.Main) { // Switch back to the main thread to update the UI
//                 textView.text = birds.joinToString("\n") { "${it.species}: ${it.count}" }
//             }
//         }
//     }

//     companion object {
//         fun newIntent(packageContext: Context, birdList: Array<String>): Intent {
//
            return Intent(packageContext, LogBookActivity::class.java).apply {
                putExtra(EXTRA_BIRD_LIST, birdList)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        val birdList = intent.getStringArrayListExtra(EXTRA_BIRD_LIST)
            ?.map {it.replace("\"","")}
            ?: arrayListOf()
        val seenBirds = birds.filter {
            birdList.contains(it.name)
        }
        setContent {
            GridScreen(seenBirds)
        }
    }

    @Composable
    fun GridScreen (birds : List<BirdResource>, modifier : Modifier = Modifier) {
        Spacer(modifier= Modifier.height(100.dp))
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns= GridCells.Fixed(2) , content= {
                items(birds){
                    bird ->  GridCard(bird = bird, modifier = modifier)
                }
            }
        )
    }

    @Composable
    fun GridCard (bird : BirdResource , modifier : Modifier) {
        Card(
            modifier = modifier
                .size(150.dp)
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = bird.imageResId),
                    contentDescription = bird.name,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = bird.name)
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun ActivityPreview() {
        MyApplicationTheme {

        }
    }
}