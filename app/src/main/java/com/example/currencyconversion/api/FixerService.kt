package com.example.currencyconversion.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FixerService {
    @GET("latest")
    fun getLatest(
            @Query("access_key") accessKey: String,
            @Query("symbols") symbols: String = "USD,AUD,CAD,PLN,MXN"): Call<FixerObject>

    @GET("/{date}")
    fun getByDate(
            @Path("date") date: String,
            @Query("access_key") accessKey: String,
            @Query("symbols") symbols: String = "USD,AUD,CAD,PLN,MXN"): Call<FixerObject>
}
