package com.example.bastionmobile.model


import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("role") val role: String,
    @SerializedName("position") val position: String,
    @SerializedName("location") val location: Coordinates?,
    @SerializedName("workTime") val workTime: WorkTime?,
    @SerializedName("onDuty") val onDuty: Boolean,
    @SerializedName("passwordHash") val password: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("applicationRole") val applicationRole: String
)

data class WorkTime(
    @SerializedName("workDays") val workDays: List<String>,
    @SerializedName("shiftStart") val shiftStart: String,
    @SerializedName("shiftFinish") val shiftFinish: String
)