package za.munyai.lloyd.weatherforecast.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import za.munyai.lloyd.weatherforecast.models.forecast.Weather
import za.munyai.lloyd.weatherforecast.models.weekforecasts.Forecast
import za.munyai.lloyd.weatherforecast.repository.WeatherAppRepository
import za.munyai.lloyd.weatherforecast.utils.network.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel@Inject constructor(private val repository: WeatherAppRepository): ViewModel() {
    private val weatherDataState: MutableSharedFlow<DataState<Weather>> = MutableSharedFlow(replay = 1)
    var weatherResponse : Flow<DataState<Weather>> = weatherDataState

    private val forecastsDataState: MutableSharedFlow<DataState<Forecast>> = MutableSharedFlow(replay = 1)
    var forecastsResponse : Flow<DataState<Forecast>> = forecastsDataState

    suspend fun getTodayWeather(lat: String, lon: String, key: String){
        getWeeklyWeather(lat, lon, key)
        viewModelScope.launch {
            repository.getTodayWeather(lat = lat, lon = lon, key = key).collectLatest {
                weatherDataState.tryEmit(it)
            }
        }
    }
    private suspend fun getWeeklyWeather(lat: String, lon: String, key: String){
        viewModelScope.launch {
            repository.getWeeklyWeather(lat = lat, lon = lon, key = key).collectLatest {
                forecastsDataState.tryEmit(it)
            }
        }
    }
}