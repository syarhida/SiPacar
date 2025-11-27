package com.syarhida.sipacar.data.api

import com.syarhida.sipacar.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface untuk API Service
 * Menggunakan Retrofit untuk mengambil data cuaca
 */
interface WeatherApiService {
    
    /**
     * Mendapatkan data prakiraan cuaca Jakarta
     * 
     * @param latitude Latitude Jakarta (-6.2)
     * @param longitude Longitude Jakarta (106.8)
     * @param hourly Parameter data yang diminta (temperature_2m, relative_humidity_2m)
     * @param forecast_days Jumlah hari prakiraan (default 7)
     * @return Response berisi data prakiraan cuaca
     */
    @GET("v1/forecast")
    suspend fun getWeatherForecast(
        @Query("latitude") latitude: Double = -6.2,
        @Query("longitude") longitude: Double = 106.8,
        @Query("hourly") hourly: String = "temperature_2m,relative_humidity_2m",
        @Query("forecast_days") forecastDays: Int = 7
    ): Response<WeatherResponse>
}

