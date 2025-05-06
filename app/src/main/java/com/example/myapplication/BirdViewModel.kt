package com.example.myapplication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val BIRD_IN_YARD_KEY = "BIRD_IN_YARD_KEY"
private val emptyArrayList: ArrayList<String> = ArrayList()

class BirdViewModel (private val savedStateHandle : SavedStateHandle) : ViewModel() {

    var birdsInYard : ArrayList<String>
        get() = savedStateHandle.get(BIRD_IN_YARD_KEY) ?: emptyArrayList
        set(value) = savedStateHandle.set(BIRD_IN_YARD_KEY, value)

}