package za.munyai.lloyd.weatherforecast.widgets.navigation

sealed class WeatherAppNavScreens(val name: String) {
    object HomeScreen: WeatherAppNavScreens("home_screen")
}