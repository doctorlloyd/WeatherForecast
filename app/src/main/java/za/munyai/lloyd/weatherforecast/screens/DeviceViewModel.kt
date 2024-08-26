package za.munyai.lloyd.weatherforecast.screens

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import za.munyai.lloyd.weatherforecast.utils.location.LocationTracker
import javax.inject.Inject

@HiltViewModel
class DeviceViewModel@Inject constructor(private val locationTracker: LocationTracker) : ViewModel() {
    var currentLocation by mutableStateOf<Location?>(null)
    fun getCurrentLocation() { viewModelScope.launch { currentLocation = locationTracker.getCurrentLocation() } }
}