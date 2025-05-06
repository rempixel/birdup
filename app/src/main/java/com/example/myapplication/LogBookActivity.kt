package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.core.view.setPadding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val EXTRA_BIRD_LIST = "com.example.myapplication.bird_list"
class LogBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_book)

        val scrollView = ScrollView(this).apply {
            setPadding(256) // Add padding to prevent content from being hidden
        }
        val textView = TextView(this)
        scrollView.addView(textView)
        setContentView(scrollView) // Set the ScrollView as the content view

        // Initialize the database
        val database = AppDatabase.getInstance(applicationContext) // Use singleton instance

        // Populate the database with sample data for testing if needed
//        database.populateWithSampleData()

        // Fetch and display bird data on a background thread
        lifecycleScope.launch(Dispatchers.IO) { // Use Dispatchers.IO for background work
            val birdDao = database.birdDao()
            val birds = birdDao.getAllBirds()
            android.util.Log.d("LogBookActivity", "Fetched birds: $birds")
            withContext(Dispatchers.Main) { // Switch back to the main thread to update the UI
                textView.text = birds.joinToString("\n") { "${it.species}: ${it.count}" }
            }
        }
    }

    companion object {
        fun newIntent(packageContext: Context, birdList: Array<String>): Intent {
            return Intent(packageContext, LogBookActivity::class.java).apply {
                putExtra(EXTRA_BIRD_LIST, birdList)
            }
        }
    }
}