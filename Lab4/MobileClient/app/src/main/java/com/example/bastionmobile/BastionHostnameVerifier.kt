package com.example.bastionmobile


import okhttp3.OkHttpClient
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

class BastionHostnameVerifier : HostnameVerifier {
    override fun verify(hostname: String?, session: SSLSession?): Boolean {
        return hostname == "192.168.56.1"
    }
}

fun createOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .hostnameVerifier(BastionHostnameVerifier())
        .build()
}