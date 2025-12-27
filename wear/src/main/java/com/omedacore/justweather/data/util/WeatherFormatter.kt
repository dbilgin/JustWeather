package com.omedacore.justweather.data.util

import com.omedacore.justweather.data.model.UnitSystem
import com.omedacore.justweather.data.model.WeatherCondition
import com.omedacore.justweather.data.model.WeatherResponse
import kotlin.math.roundToInt
import java.util.Locale

object WeatherFormatter {
    /**
     * Formats temperature with unit based on the unit system.
     * @param weather The weather response containing temperature data
     * @param unitSystem The unit system (METRIC or IMPERIAL)
     * @return Formatted temperature string like "22°C" or "72°F"
     */
    fun formatTemperature(weather: WeatherResponse, unitSystem: UnitSystem): String {
        val temp = weather.main.temp.roundToInt()
        return if (unitSystem == UnitSystem.METRIC) {
            "${temp}°C"
        } else {
            "${temp}°F"
        }
    }

    /**
     * Formats wind speed with unit and locale-aware formatting.
     * @param weather The weather response containing wind data
     * @param unitSystem The unit system (METRIC or IMPERIAL)
     * @param locale The locale for number formatting
     * @return Formatted wind speed string like "10.50 km/h" or "6.52 mph"
     */
    fun formatWindSpeed(weather: WeatherResponse, unitSystem: UnitSystem, locale: Locale): String {
        return if (unitSystem == UnitSystem.METRIC) {
            // Convert m/s to km/h
            val speedKmh = weather.wind.speed * 3.6
            String.format(locale, "%.2f km/h", speedKmh)
        } else {
            String.format(locale, "%.2f mph", weather.wind.speed)
        }
    }

    /**
     * Extracts weather condition from the weather response with fallback.
     * @param weather The weather response
     * @return WeatherCondition object, or empty condition if none found
     */
    fun getCondition(weather: WeatherResponse): WeatherCondition {
        return weather.weather.firstOrNull() ?: WeatherCondition()
    }

    /**
     * Gets weather icon resource ID using WeatherIconHelper.
     * @param weather The weather response
     * @return Resource ID for the weather icon
     */
    fun getIconResId(weather: WeatherResponse): Int {
        val condition = getCondition(weather)
        val conditionCode = condition.conditionCode
        val sunrise = weather.sys.sunrise
        val sunset = weather.sys.sunset
        return WeatherIconHelper.getWeatherIconResId(conditionCode, sunrise, sunset)
    }
}

