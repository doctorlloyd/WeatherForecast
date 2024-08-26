package za.munyai.lloyd.weatherforecast.utils.location

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
}