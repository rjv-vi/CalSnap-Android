package com.calsnap.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calsnap.app.data.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    // null = загружается, false = не пройден, true = пройден
    private val _onboardingDone = MutableStateFlow<Boolean?>(null)
    val onboardingDone = _onboardingDone.asStateFlow()

    init {
        viewModelScope.launch {
            _onboardingDone.value = userPreferences.isOnboardingDone()
        }
    }

    fun markOnboardingDone() {
        _onboardingDone.value = true
    }
}
