package com.example.weather_report.model.repository

import com.example.weather_report.model.local.ICityLocalDataSource
import com.example.weather_report.model.local.ICurrentWeatherLocalDataSource
import com.example.weather_report.model.local.IForecastItemLocalDataSource
import com.example.weather_report.model.pojo.City
import com.example.weather_report.model.pojo.Clouds
import com.example.weather_report.model.pojo.Coordinates
import com.example.weather_report.model.pojo.ForecastItem
import com.example.weather_report.model.pojo.ForecastResponse
import com.example.weather_report.model.pojo.MainWeather
import com.example.weather_report.model.pojo.Sys
import com.example.weather_report.model.pojo.Weather
import com.example.weather_report.model.pojo.Wind
import com.example.weather_report.model.remote.IWeatherAndForecastRemoteDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.Test
// imports necessary for singleton reflection
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.KMutableProperty1

class WeatherRepositoryImplTest {

    private lateinit var cityLocal: ICityLocalDataSource
    private lateinit var forecastLocal: IForecastItemLocalDataSource
    private lateinit var currentWeatherLocal: ICurrentWeatherLocalDataSource
    private lateinit var remote: IWeatherAndForecastRemoteDataSource
    private lateinit var repository: WeatherRepositoryImpl

    val main1 = MainWeather(
        temp = 296.76,
        feels_like = 296.98,
        temp_min = 296.76,
        temp_max = 297.87,
        pressure = 1015,
        sea_level = 1015,
        grnd_level = 933,
        humidity = 69,
        temp_kf = -1.11
    )

    val weather1 = listOf(
        Weather(
            id = 500,
            main = "Rain",
            description = "light rain",
            icon = "10d"
        )
    )

    val clouds1 = Clouds(all = 100)

    val wind1 = Wind(
        speed = 0.62,
        deg = 349,
        gust = 1.18
    )

    val sys1 = Sys(
        pod = "d",
        type = 0,
        id = 1,
        country = "Hi",
        sunrise = 123,
        sunset = 456
    )


    val main2 = MainWeather(
        temp = 295.45,
        feels_like = 295.59,
        temp_min = 292.84,
        temp_max = 295.45,
        pressure = 1015,
        sea_level = 1015,
        grnd_level = 931,
        humidity = 71,
        temp_kf = 2.61
    )

    val weather2 = listOf(
        Weather(
            id = 500,
            main = "Rain",
            description = "light rain",
            icon = "10n"
        )
    )

    val clouds2 = Clouds(all = 96)

    val wind2 = Wind(
        speed = 1.97,
        deg = 157,
        gust = 3.39
    )

    val sys2 = Sys(
    pod = "d",
    type = 0,
    id = 1,
    country = "Hi",
    sunrise = 123,
    sunset = 456
    )

    val mockForecastItems = listOf(
        ForecastItem(
            dt = 1661871600,
            main = main1,
            weather = weather1,
            clouds = clouds1,
            wind = wind1,
            visibility = 10000,
            pop = 0.32,
            sys = sys1,
            dt_txt = "2022-08-30 15:00:00",
            cityId = 3163858
        ),
        ForecastItem(
            dt = 1661882400,
            main = main2,
            weather = weather2,
            clouds = clouds2,
            wind = wind2,
            visibility = 10000,
            pop = 0.33,
            sys = sys2,
            dt_txt = "2022-08-30 18:00:00",
            cityId = 3163858
        )
    )

    val mockResponse = ForecastResponse(
        list = mockForecastItems,
        cod = "hehe",
        message = 97,
        cnt = 25,
        city = City(
            8627,
            "Iredya",
            Coordinates(
                1.0,
                1.0
            ),
            "Mirrayon",
            0,
            0,
            0,
            0,
            true
        )
    )

    private fun resetRepoInstanceByReflection() {
        // get the companion object instance
        val companionInstance = WeatherRepositoryImpl::class.companionObjectInstance
        val companion = WeatherRepositoryImpl::class.companionObject

        if (companion != null && companionInstance != null) {
            // find the property named "repo"
            val repoProperty = companion.declaredMemberProperties
                .firstOrNull { it.name == "repo" } as? KMutableProperty1<Any, Any?>

            // make the property accessible and set it to null
            repoProperty?.let {
                it.isAccessible = true
                it.set(companionInstance, null)
            }
        }
    }

    @Before
    fun initEnv() {

        // prevent repo singleton behaviour by reflection
        resetRepoInstanceByReflection()

        cityLocal = mockk(relaxed = true)
        forecastLocal = mockk(relaxed = true)
        currentWeatherLocal = mockk(relaxed = true)
        remote = mockk(relaxed = true)
        repository = WeatherRepositoryImpl.getInstance(
            cityLocal, forecastLocal, currentWeatherLocal, remote
        )
    }

    @After
    fun tearDown() {
        resetRepoInstanceByReflection()
    }

    @Test
    fun fetchForecastDataRemotely_takesValidCoordinates_returnsDataFromRemoteSource() = runTest {
        coEvery { remote.makeNetworkCallToGetForecast(1.0, 1.0, "metric", "en") } returns mockResponse

        val result = repository.fetchForecastDataRemotely(1.0, 1.0, "metric", "en")

        Assertions.assertEquals(mockResponse, result)
        coVerify { remote.makeNetworkCallToGetForecast(1.0, 1.0, "metric", "en") }
    }
}