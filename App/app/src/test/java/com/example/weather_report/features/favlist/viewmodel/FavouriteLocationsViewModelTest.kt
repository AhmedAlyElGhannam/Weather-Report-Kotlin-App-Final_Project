package com.example.weather_report.features.favlist.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.weather_report.model.pojo.entity.LocationWithWeather
import com.example.weather_report.model.repository.IWeatherRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavouriteLocationsViewModelTest {
    private lateinit var viewModel: FavouriteLocationsViewModel
    private lateinit var repository: IWeatherRepository
    private val testDispatcher = StandardTestDispatcher()
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initEnv() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = FavouriteLocationsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadFavouriteLocations_shouldUpdateLiveData() = runTest {
        // Given
        val mockList = listOf<LocationWithWeather>(mockk(), mockk())
        // stub returns mock list
        coEvery { repository.getFavouriteLocationsWithWeather() } returns mockList

        val observer = mockk<Observer<List<LocationWithWeather>>>(relaxed = true)
        viewModel.favoriteLocations.observeForever(observer)

        // When
        viewModel.loadFavouriteLocations()
        advanceUntilIdle()

        // Then (results are verified via observer)
        verify { observer.onChanged(mockList) }
    }

    @Test
    fun removeFavourite_shouldReloadFavouriteLocations() = runTest {
        // Given:
        val locationId = "loc123"
        val mockList = listOf<LocationWithWeather>(mockk())

        // stub returns nothing
        coEvery { repository.removeFavouriteLocation(locationId) } just Runs
        // stub returns mock list
        coEvery { repository.getFavouriteLocationsWithWeather() } returns mockList

        val observer = mockk<Observer<List<LocationWithWeather>>>(relaxed = true)
        viewModel.favoriteLocations.observeForever(observer)

        // When
        viewModel.removeFavourite(locationId)
        advanceUntilIdle()

        // Then (results are verified via observer)
        coVerifySequence {
            repository.removeFavouriteLocation(locationId)
            repository.getFavouriteLocationsWithWeather()
        }
        verify { observer.onChanged(mockList) }
    }
}