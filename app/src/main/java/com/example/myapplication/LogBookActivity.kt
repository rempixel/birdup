package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.widget.ScrollView
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val EXTRA_BIRD_LIST = "com.example.myapplication.bird_list"
class LogBookActivity : AppCompatActivity() {

    private val birds = listOf(
        BirdResource(R.drawable.bird1, "Rock Pigeon"),
        BirdResource(R.drawable.bird2, "Sparrow"),
        BirdResource(R.drawable.bird3, "Cardinal"),
        BirdResource(R.drawable.bird4, "Blue Jay"),
    )
    private var seenBirds = emptyList<BirdResource>()
    private val logHistory = mutableStateOf(emptyList<BirdResource>())

    data class BirdResource(val imageResId : Int, val name : String)

    companion object {
        fun newIntent(packageContext: Context, birdList: ArrayList<String>): Intent {
            requireNotNull(packageContext) { "Context cannot be Null" }
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
            ?.map { birdName->
                BirdResource(
                    imageResId = when (birdName) {
                        "Sparrow" -> R.drawable.bird2
                        "Rock Pigeon" -> R.drawable.bird1
                        "Cardinal" -> R.drawable.bird3
                        "Blue Jay" -> R.drawable.bird4
                        else -> R.drawable.placeholder
                    },
                    name=birdName
                )
            }?: emptyList()

        val database = AppDatabase.getInstance(applicationContext) //initalization

        database.populateWithSampleData()

        lifecycleScope.launch(Dispatchers.IO) {
            val birdDao = database.birdDao()
            val birds = birdDao.getAllBirds()
            val additionalBirds = birds.map { birdEntity ->
                BirdResource(
                    imageResId = birdEntity.imageResId.takeIf { it != 0}?: R.drawable.placeholder,
                    name = birdEntity.species
                )
            }
            //Add seen birds to database
            for(bird in birdList){
                database.addSeenBirdToDB(bird)
            }

            val combinedBirds = (birdList + additionalBirds).distinctBy{ it.name }
            android.util.Log.d("LogBookActivity", "Fetched birds: $birds")
            withContext(Dispatchers.Main) {
                logHistory.value = combinedBirds
            }
        }
        setContent {
            GridScreen(logHistory.value)
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