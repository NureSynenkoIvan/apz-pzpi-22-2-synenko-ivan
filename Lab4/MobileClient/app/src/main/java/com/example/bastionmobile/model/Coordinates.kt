package com.example.bastionmobile.model

import com.google.gson.annotations.SerializedName
import java.util.Objects
import kotlin.math.pow
import kotlin.math.sqrt

data class Coordinates(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
) {

    init {
        require(latitude in -90.0..90.0) { "Latitude must be between -90 and 90" }
        require(longitude in -180.0..180.0) { "Longitude must be between -180 and 180" }
    }

    fun distanceTo(other: Coordinates): Double {
        return sqrt((latitude - other.latitude).pow(2) + (longitude - other.longitude).pow(2))
    }

    override fun toString(): String {
        return "Coordinates [\n  latitude=$latitude,\n  longitude=$longitude \n]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Coordinates

        if (java.lang.Double.compare(latitude, other.latitude) != 0) return false
        if (java.lang.Double.compare(longitude, other.longitude) != 0) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(latitude, longitude)
    }
}