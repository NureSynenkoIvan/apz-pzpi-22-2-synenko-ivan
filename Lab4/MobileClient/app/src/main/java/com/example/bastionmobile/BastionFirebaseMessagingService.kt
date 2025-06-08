package com.example.bastionmobile

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


class BastionFirebaseMessagingService : FirebaseMessagingService() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST = 1001;
    private val GEOLOCATION_COMMAND = "SEND_LOCATION"


    @RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            val messageFromData = remoteMessage.data["message"]

            if (messageFromData.equals(GEOLOCATION_COMMAND)) {

                CoroutineScope(Dispatchers.IO).launch {
                    postCurrentLocation()
                }
                return
            } else {
                messageFromData?.let { message ->
                    val intent = Intent(this, AlertInformationActivity::class.java).apply {
                        putExtra("message", message)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                }
                    sendNotification(messageFromData.toString())
            }
        }
    }

    @RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
    private suspend fun postCurrentLocation() {
        val client = createOkHttpClient()
        var url = "https://192.168.56.1:8080/geo?fcmToken=${FirebaseMessaging.getInstance().token.await()}"
        val emptyBody = "".toRequestBody("application/json".toMediaTypeOrNull())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude

                url = url + "&lat=${lat}&lon=${lon}"

                val request = Request.Builder()
                    .url(url)
                    .post(emptyBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {

                    } else {

                    }

            }
        }

        val lat = 49.78905119940299
        val lon = 36.41871499435669
        url = url + "&lat=${lat}&lon=${lon}"
        val request = Request.Builder()
            .url(url)
            .post(emptyBody)
            .build()

        val response = client.newCall(request).execute()

    }

    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, AlertInformationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("FCM Message")
            .setContentText(messageBody)
            .setAutoCancel(false)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}