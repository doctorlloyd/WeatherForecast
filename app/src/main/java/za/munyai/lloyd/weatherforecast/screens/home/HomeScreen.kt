package za.munyai.lloyd.weatherforecast.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import za.munyai.lloyd.weatherforecast.R
import za.munyai.lloyd.weatherforecast.models.forecast.Weather
import za.munyai.lloyd.weatherforecast.models.weekforecasts.Forecast
import za.munyai.lloyd.weatherforecast.screens.DeviceViewModel
import za.munyai.lloyd.weatherforecast.utils.*
import za.munyai.lloyd.weatherforecast.widgets.ConnectivityViewModel
import za.munyai.lloyd.weatherforecast.utils.network.DataState
import za.munyai.lloyd.weatherforecast.widgets.loader.Loader
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("SuspiciousIndentation")
@Suppress("DEPRECATION")
@Composable
fun HomeScreen() {
    // Used for side effects on submit button
    val viewModelJob = Job()
    val scope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // User view model
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val connectivityViewModel = hiltViewModel<ConnectivityViewModel>()
    val deviceViewModel = hiltViewModel<DeviceViewModel>()
    deviceViewModel.getCurrentLocation()

    val progressBar = remember { mutableStateOf(false) }
    val weather = remember { mutableStateOf(Weather())}

    val weeklyWeather = remember { mutableStateOf(Forecast())}

    if(!weather.value.name.isNullOrEmpty() && weeklyWeather.value.list!!.isNotEmpty()) WeatherWidget(weather = weather, weeklyWeather = weeklyWeather)
    else scope.launch {
        if(connectivityViewModel.appConnectivityStatus() && deviceViewModel.currentLocation?.latitude != null){
            withContext(Dispatchers.IO) {
                homeViewModel.getTodayWeather(lat = deviceViewModel.currentLocation?.latitude.toString(), lon = deviceViewModel.currentLocation?.longitude.toString(), key = Constants.API_KEY)
                withContext(Dispatchers.Main) {
                    homeViewModel.weatherResponse.collectLatest {
                        when(it){
                            is DataState.Success-> {
                                weather.value = it.data
                                homeViewModel.forecastsResponse.collectLatest { week ->
                                    when(week) {
                                        is DataState.Success -> { progressBar.value = false
                                            weeklyWeather.value = week.data }
                                        is DataState.Loading -> {}
                                        is DataState.Error -> progressBar.value = false
                                    }
                                }
                            }
                            is DataState.Loading -> progressBar.value = true
                            is DataState.Error -> progressBar.value = false
                        }
                    }
                }
            }
        }
    }
    if(progressBar.value) Loader()
}

@Composable
fun WeatherWidget(weather: MutableState<Weather>, weeklyWeather: MutableState<Forecast>){
    val weatherList: MutableList<Weather> = mutableListOf()
    if(weeklyWeather.value.list?.isNotEmpty() == true){
        for (w in weeklyWeather.value.list!!){
            if(isBetween11AM2PM(dateString = w.dt_txt!!, start = 11, end = 14)){
                weatherList.add(w)
            }
        }
    }

    if(weatherList.size >= 5) Box(modifier = Modifier.fillMaxSize().padding(0.dp)){
        Column(modifier = Modifier.fillMaxSize().paint(painterResource(id =
        if(backgroundColorChanger(weather.value.weather?.get(0)?.icon!!).contains("clear", ignoreCase = true)) R.drawable.sunny
        else if(backgroundColorChanger(weather.value.weather?.get(0)?.icon!!).contains("rain", ignoreCase = true)) R.drawable.rainy
        else R.drawable.cloudy
        ), contentScale = ContentScale.FillBounds)) {
            Box(modifier = Modifier.fillMaxWidth().padding(12.dp)){
                Text(text = "5 Days Forecast",style = TextStyle(letterSpacing = 0.sp, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 28.sp),
                    modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = 4.dp))
            }
            Divider(color = Color.White, thickness = 2.dp, modifier = Modifier.fillMaxWidth())
            LazyColumn(modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 30.dp, top = 4.dp, bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(getWeekDays().size) { i ->
                    Card(modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 2.dp), backgroundColor = Color.White, border = BorderStroke(1.dp, MaterialTheme.colors.surface)){
                        Box(modifier = Modifier.fillMaxSize().padding(end = 8.dp, start = 10.dp)){
                            Column(modifier = Modifier.align(Alignment.CenterStart).padding(8.dp)){
                                Text(style = TextStyle(letterSpacing = 0.sp, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 24.sp), textAlign = TextAlign.Center, text = getWeekDays()[i])
                                Image(painter = painterResource(id = getWeatherIcon(weatherList[i].weather!![0].icon!!)), contentDescription = null)
                            }
                            Text(style = TextStyle(letterSpacing = 0.sp, fontWeight = FontWeight.SemiBold, fontSize = 36.sp, lineHeight = 36.sp), modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp), textAlign = TextAlign.Start, text = "${"%.0f".format(convertTemperature(weatherList[i].main?.temp!!))}\u00B0")
                        }
                    }
                }
            }
        }
    }
}