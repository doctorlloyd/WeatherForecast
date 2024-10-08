package za.munyai.lloyd.weatherforecast.data

import retrofit2.http.GET
import retrofit2.http.Query
import za.munyai.lloyd.weatherforecast.models.forecast.Weather
import za.munyai.lloyd.weatherforecast.models.weekforecasts.Forecast

interface WeatherApiService {
    @GET("/data/2.5/weather")
    suspend fun getTodayWeather(@Query("lat") lat: String,
                                @Query("lon") lon: String,
                                @Query("appid") key: String): Weather

    @GET("/data/2.5/forecast")
    suspend fun getWeeklyWeather(@Query("lat") lat: String,
                                 @Query("lon") lon: String,
                                 @Query("appid") key: String): Forecast
}