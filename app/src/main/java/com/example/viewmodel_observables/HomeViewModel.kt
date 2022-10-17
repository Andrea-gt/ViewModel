package com.example.viewmodel_observables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
private const val STATE_LOADING_TIME = 2000L

class HomeViewModel: ViewModel() {

    private val stateOfHome = MutableStateFlow<HomeViewState>(HomeViewState.Default)
    val homeState: StateFlow<HomeViewState> = stateOfHome

    fun changeState(state: HomeViewState) {
        viewModelScope.launch {
            stateOfHome.value = HomeViewState.Loading(currentState = state)
            delay(STATE_LOADING_TIME)
            stateOfHome.value = state
        }
    }
    sealed class HomeViewState {
        object Default: HomeViewState()
        object Success: HomeViewState()
        object Failure: HomeViewState()
        object Empty: HomeViewState()
        class Loading(val currentState: HomeViewState = HomeViewState.Default): HomeViewState()
    }

}