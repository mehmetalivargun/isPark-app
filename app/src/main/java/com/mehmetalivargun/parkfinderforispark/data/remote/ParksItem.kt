package com.mehmetalivargun.parkfinderforispark.data.remote


import com.google.gson.annotations.SerializedName

data class ParksItem(
    @SerializedName("capacity")
    val capacity: Int,
    @SerializedName("district")
    val district: String,
    @SerializedName("emptyCapacity")
    val emptyCapacity: Int,
    @SerializedName("freeTime")
    val freeTime: Int,
    @SerializedName("isOpen")
    val isOpen: Int,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lng")
    val lng: String,
    @SerializedName("parkID")
    val parkID: Int,
    @SerializedName("parkName")
    val parkName: String,
    @SerializedName("parkType")
    val parkType: String,
    @SerializedName("workHours")
    val workHours: String
)