package za.munyai.lloyd.weatherforecast.models.forecast

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coord(
    @Json(name = "lat")val lat: Double? = 0.00,
    @Json(name = "lon")val lon: Double? = 0.00
) : Parcelable