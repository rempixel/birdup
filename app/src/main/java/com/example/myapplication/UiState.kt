package com.example.myapplication

import androidx.compose.runtime.Immutable

@Immutable
class UiState (
    val inferenceTime : Long = 0L,
    val categories : List<SongClassificationHelper.Category> = emptyList(),
    val setting: Setting = Setting(),
)

@Immutable
data class Setting (
    val location: String =  SongClassificationHelper.DEFAULT_LOCATION //by default (if location data is not enabled)
)