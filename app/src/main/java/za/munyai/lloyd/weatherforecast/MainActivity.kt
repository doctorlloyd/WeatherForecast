package za.munyai.lloyd.weatherforecast

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import za.munyai.lloyd.weatherforecast.screens.DeviceViewModel
import za.munyai.lloyd.weatherforecast.utils.networkConnection.ConnectivityManager
import za.munyai.lloyd.weatherforecast.utils.theme.WeatherForecastTheme
import za.munyai.lloyd.weatherforecast.widgets.navigation.WeatherAppNavWidget
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var connectivityManager: ConnectivityManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val viewModel: DeviceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherForecast()
        }
        connectivityManager.registerConnectionObserver(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStart() {
        super.onStart()
        getActualLocation(viewModel)
        connectivityManager.registerConnectionObserver(this)
    }
    override fun onResume() {
        super.onResume()
        connectivityManager.registerConnectionObserver(this)
        getActualLocation(viewModel)
    }
    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterConnectionObserver(this)

        Thread.currentThread().interrupt()
        finishAffinity()
        exitProcess(0)
    }

    private fun getActualLocation(viewModel: DeviceViewModel) {
        val task = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }

        task.addOnSuccessListener {
            if (it != null){
                viewModel.getCurrentLocation()
            }
        }
    }
}

@Composable
fun WeatherForecast() {
    WeatherForecastTheme {
        WeatherAppNavWidget()
    }
}