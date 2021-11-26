package com.mehmetalivargun.parkfinderforispark.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IsparkApi {
    @GET("Park")
    suspend fun getAllParks(): Response<Parks>

    @GET("ParkDetay")
    suspend fun getParkDetail(@Query("id") id :Int):Response<ParkDetail>

}