package com.example.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

private const val EXTRA_BIRD_LIST = "com.example.myapplication.bird_list"
class LogBookActivity : AppCompatActivity () {
    private var birdSeen = false // default have not seen bird before


    companion object {
        fun newIntent (packageContext : Context, birdList : Array<String> ) : Intent {
            return Intent(packageContext, LogBookActivity::class.java).apply{
                putExtra(EXTRA_BIRD_LIST, birdList)
            }
        }
    }
}