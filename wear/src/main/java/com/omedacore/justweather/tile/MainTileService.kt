package com.omedacore.justweather.tile

import android.content.Context
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.Colors
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.tooling.preview.Preview
import androidx.wear.tiles.tooling.preview.TilePreviewData
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import com.omedacore.justweather.data.local.PreferencesManager
import com.omedacore.justweather.data.model.UnitSystem
import com.omedacore.justweather.data.repository.WeatherRepository
import com.omedacore.justweather.data.util.WeatherFormatter
import com.omedacore.justweather.presentation.MainActivity

private const val RESOURCES_VERSION = "0"

/**
 * Skeleton for a tile with no images.
 */
@OptIn(ExperimentalHorologistApi::class)
class MainTileService : SuspendingTileService() {

    private val repository by lazy {
        val preferencesManager = PreferencesManager(applicationContext)
        WeatherRepository(preferencesManager)
    }

    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ) = resources()

    override suspend fun tileRequest(
        requestParams: RequestBuilders.TileRequest
    ) = Companion.tile(requestParams, this, repository)

    companion object {
        suspend fun tile(
            requestParams: RequestBuilders.TileRequest,
            context: Context,
            repository: WeatherRepository
        ): TileBuilders.Tile {
        val city = repository.getSavedCity()
        val unitSystem = repository.getUnitSystem() ?: UnitSystem.METRIC
        
        val weather = if (city != null) {
            repository.getCurrentWeather(city).getOrNull()
        } else {
            null
        }

        val singleTileTimeline = TimelineBuilders.Timeline.Builder()
            .addTimelineEntry(
                TimelineBuilders.TimelineEntry.Builder()
                    .setLayout(
                        LayoutElementBuilders.Layout.Builder()
                            .setRoot(tileLayout(requestParams, context, weather, unitSystem))
                            .build()
                    )
                    .build()
            )
            .build()

        return TileBuilders.Tile.Builder()
            .setResourcesVersion(RESOURCES_VERSION)
            .setTileTimeline(singleTileTimeline)
            .build()
        }
    }
}

private fun tileLayout(
    requestParams: RequestBuilders.TileRequest,
    context: Context,
    weather: com.omedacore.justweather.data.model.WeatherResponse?,
    unitSystem: UnitSystem
): LayoutElementBuilders.LayoutElement {
    if (weather == null) {
        return PrimaryLayout.Builder(requestParams.deviceConfiguration)
            .setResponsiveContentInsetEnabled(true)
            .setContent(
                Text.Builder(context, "No weather data")
                    .setColor(argb(Colors.DEFAULT.onSurface))
                    .setTypography(Typography.TYPOGRAPHY_BODY1)
                    .build()
            )
            .build()
    }

    val temperature = WeatherFormatter.formatTemperature(weather, unitSystem)
    val condition = WeatherFormatter.getCondition(weather)
    val conditionText = condition.description.ifEmpty { condition.main }

    // Create column layout for essential weather information only
    // Tiles are not scrollable, so we keep it minimal
    val content = LayoutElementBuilders.Column.Builder()
        .addContent(
            // City name
            Text.Builder(context, weather.name)
                .setColor(argb(Colors.DEFAULT.onSurface))
                .setTypography(Typography.TYPOGRAPHY_TITLE2)
                .build()
        )
        .addContent(
            // Spacer
            LayoutElementBuilders.Spacer.Builder()
                .setHeight(DimensionBuilders.DpProp.Builder(4f).build())
                .build()
        )
        .addContent(
            // Temperature (large)
            Text.Builder(context, temperature)
                .setColor(argb(Colors.DEFAULT.onSurface))
                .setTypography(Typography.TYPOGRAPHY_DISPLAY1)
                .build()
        )
        .addContent(
            // Spacer
            LayoutElementBuilders.Spacer.Builder()
                .setHeight(DimensionBuilders.DpProp.Builder(4f).build())
                .build()
        )
        .addContent(
            // Weather condition
            Text.Builder(context, conditionText)
                .setColor(argb(Colors.DEFAULT.onSurface))
                .setTypography(Typography.TYPOGRAPHY_BODY2)
                .build()
        )
        .addContent(
            // Spacer
            LayoutElementBuilders.Spacer.Builder()
                .setHeight(DimensionBuilders.DpProp.Builder(4f).build())
                .build()
        )
        .addContent(
            // OpenWeather attribution text
            Text.Builder(context, "OpenWeather")
                .setColor(argb(Colors.DEFAULT.primary))
                .setTypography(Typography.TYPOGRAPHY_CAPTION2)
                .build()
        )
        .setModifiers(
            ModifiersBuilders.Modifiers.Builder()
                .setPadding(
                    ModifiersBuilders.Padding.Builder()
                        .setStart(DimensionBuilders.DpProp.Builder(8f).build())
                        .setEnd(DimensionBuilders.DpProp.Builder(8f).build())
                        .setTop(DimensionBuilders.DpProp.Builder(8f).build())
                        .setBottom(DimensionBuilders.DpProp.Builder(8f).build())
                        .build()
                )
                .setClickable(
                    ModifiersBuilders.Clickable.Builder()
                        .setOnClick(
                            ActionBuilders.LaunchAction.Builder()
                                .setAndroidActivity(
                                    ActionBuilders.AndroidActivity.Builder()
                                        .setClassName(MainActivity::class.java.name)
                                        .setPackageName(context.packageName)
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build()
        )
        .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
        .build()

    return PrimaryLayout.Builder(requestParams.deviceConfiguration)
        .setResponsiveContentInsetEnabled(true)
        .setContent(content)
        .build()
}

private fun resources(): ResourceBuilders.Resources {
    // Resources version is required for tile caching/updates
    // Actions are defined inline in layout modifiers, so no resources to register
    return ResourceBuilders.Resources.Builder()
        .setVersion(RESOURCES_VERSION)
        .build()
}

@Preview(device = WearDevices.SMALL_ROUND)
@Preview(device = WearDevices.LARGE_ROUND)
fun tilePreview(context: Context) = TilePreviewData(
    { _ -> resources() }
) { requestParams ->
    kotlinx.coroutines.runBlocking {
        val preferencesManager = PreferencesManager(context)
        val repository = WeatherRepository(preferencesManager)
        MainTileService.tile(requestParams, context, repository)
    }
}