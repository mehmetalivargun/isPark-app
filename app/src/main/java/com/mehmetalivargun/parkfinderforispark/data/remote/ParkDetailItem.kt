package com.mehmetalivargun.parkfinderforispark.data.remote


import com.google.gson.annotations.SerializedName

data class ParkDetailItem(
    @SerializedName("address")
    val address: String,
    @SerializedName("areaPolygon")
    val areaPolygon: String,
    @SerializedName("capacity")
    val capacity: Int,
    @SerializedName("district")
    val district: String,
    @SerializedName("emptyCapacity")
    val emptyCapacity: Int,
    @SerializedName("freeTime")
    val freeTime: Int,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lng")
    val lng: String,
    @SerializedName("locationName")
    val locationName: String,
    @SerializedName("monthlyFee")
    val monthlyFee: Double,
    @SerializedName("parkID")
    val parkID: Int,
    @SerializedName("parkName")
    val parkName: String,
    @SerializedName("parkType")
    val parkType: String,
    @SerializedName("tariff")
    val tariff: String,
    @SerializedName("updateDate")
    val updateDate: String,
    @SerializedName("workHours")
    val workHours: String
)