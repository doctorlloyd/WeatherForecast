package za.munyai.lloyd.weatherforecast.repository

import za.munyai.lloyd.weatherforecast.utils.network.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import za.munyai.lloyd.weatherforecast.data.WeatherApiService
import za.munyai.lloyd.weatherforecast.models.forecast.Weather
import za.munyai.lloyd.weatherforecast.models.weekforecasts.Forecast
import javax.inject.Inject

class WeatherAppRepository@Inject constructor(private val weatherApiService: WeatherApiService) {
    suspend fun getTodayWeather(lat: String, lon: String, key: String): Flow<DataState<Weather>> = flow {
        emit(DataState.Loading)
        try {
            val forecast = weatherApiService.getTodayWeather(lat = lat, lon = lon, key = key)
            emit(DataState.Success(data = forecast))
        } catch (e: Exception) {
            emit(DataState.Error(exception = e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getWeeklyWeather(lat: String, lon: String, key: String): Flow<DataState<Forecast>> = flow {
        emit(DataState.Loading)
        try {
            val forecasts = weatherApiService.getWeeklyWeather(lat = lat, lon = lon, key = key)
            emit(DataState.Success(data = forecasts))
        } catch (e: Exception) {
            emit(DataState.Error(exception = e))
        }
    }.flowOn(Dispatchers.IO)
}