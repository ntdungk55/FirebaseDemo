package com.example.firebasedemo.ui.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, "handleException: ${exception.message}")
    }

    override val coroutineContext: CoroutineContext =
        Dispatchers.Default + SupervisorJob() + coroutineExceptionHandler

    fun launchCoroutine(task: () -> Unit) = viewModelScope.launch(coroutineContext) {
        task.invoke()
    }

    companion object {
        const val TAG = "BaseViewModel"
    }
}