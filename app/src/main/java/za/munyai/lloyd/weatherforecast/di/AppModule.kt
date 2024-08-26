package za.munyai.lloyd.weatherforecast.di

import android.app.Application
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import za.munyai.lloyd.weatherforecast.data.WeatherApiService
import za.munyai.lloyd.weatherforecast.models.location.DefaultLocationTracker
import za.munyai.lloyd.weatherforecast.repository.WeatherAppRepository
import za.munyai.lloyd.weatherforecast.utils.location.LocationTracker
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun provideWeatherRepository(weatherApiService: WeatherApiService): WeatherAppRepository {
        return WeatherAppRepository(weatherApiService)
    }

    @Provides
    @Singleton
    fun providesFusedLocationProviderClient(application: Application): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)

    @Provides
    @Singleton
    fun providesLocationTracker(fusedLocationProviderClient: FusedLocationProviderClient, application: Application): LocationTracker = DefaultLocationTracker(fusedLocationProviderClient = fusedLocationProviderClient, application = application)

}