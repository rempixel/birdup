package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class MainActivity : ComponentActivity() {
    private var recorder: MediaRecorder? = null
    private var fileName: String = ""

    private val birdViewModel : BirdViewModel by viewModels()

    private val logBookLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        result ->
        if (result.resultCode == Activity.RESULT_OK)
        {

        }
    }

    fun launchLogBookActivity (intent : Intent) {
        logBookLauncher.launch(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request location permissions
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

        fileName = "${externalCacheDir?.absolutePath}/audiorecordtest.mp4"

        setContent {
            MyApplicationTheme {
                DemoScreen(
                    onStartRecording = { startRecording() },
                    onStopRecording = { stopRecording() },
                    checkPermission = { checkAudioPermission() }
                )
            }
        }
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
                start()
                Toast.makeText(this@MainActivity, "Recording started", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(this@MainActivity, "Recording failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        Toast.makeText(this@MainActivity, "Recording stopped", Toast.LENGTH_SHORT).show()

        // Send the recording to the server
        sendRecordingToServer()
    }


    private fun getCurrentDate(): Map<String, Int> {
        val calendar = Calendar.getInstance()
        return mapOf(
            "year" to calendar.get(Calendar.YEAR),
            "month" to calendar.get(Calendar.MONTH) + 1, // Months are 0-based
            "day" to calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun sendRecordingToServer() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val location: Location? = if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } else {
            null
        }

        val lat = location?.latitude ?: 0.0
        val lon = location?.longitude ?: 0.0
        val date = getCurrentDate()
        val minConf = 0.3

        // Launch a coroutine to handle the suspension function
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Call the ServerHelper function and handle the response
                ServerHelper.sendAudioFile(fileName, lat, lon, date, minConf) { response, error ->
                    // Switch to the main thread for UI updates
                    CoroutineScope(Dispatchers.Main).launch {
                        if (error != null) {
                            Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@MainActivity, "Response: $response", Toast.LENGTH_LONG).show()
                            // Handle the JSON   response here if needed
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun checkAudioPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
            false
        }
    }
}

@Composable
fun DemoScreen(
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    checkPermission: () -> Boolean
) {
    var isRecording by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.yardcropped),
            contentDescription = "Neko Atsume Yard for Placeholder",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.matchParentSize()
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (checkPermission()) {
                    if (isRecording) {
                        onStopRecording()
                    } else {
                        onStartRecording()
                    }
                    isRecording = !isRecording
                }
            }) {
                Text(if (isRecording) "Stop Recording" else "Start Recording")
            }
        }
        Column (modifier = Modifier.padding(16.dp)) {
            Spacer (modifier = Modifier.height(16.dp))

        }
    }
}

// For logging what birds you have seen
@Composable
fun LogBookScreen(
    modifier: Modifier,
    mainActivity: MainActivity,
    birdViewModel: BirdViewModel,
    onLaunchLogBook : (Intent) -> Unit
) {
    Row {
        Button (
            onClick = {
                val birdsInYard = birdViewModel.birdsInYard
                val intent = LogBookActivity.newIntent(mainActivity, birdsInYard)
                onLaunchLogBook(intent)
            },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        ){
            Text("Log Book")
        }
    }
}

// Recording setting adjustment
@Composable
fun BottomSheet(
    uiState: UiState,
    modifier: Modifier,
    location: (value: String) -> Unit,
    date: (value: String) -> Unit
) {

}

@Preview(showBackground = true)
@Composable
fun ApplicationTheme() {
    MyApplicationTheme {
        Column {
            LogBookScreen(
                modifier = Modifier,
                mainActivity = MainActivity(),
                birdViewModel = BirdViewModel(savedStateHandle = SavedStateHandle()),
                onLaunchLogBook = {}
            )
            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}


