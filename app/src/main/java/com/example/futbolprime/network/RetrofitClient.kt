package com.example.futbolprime.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton que configura y provee la instancia de Retrofit
 * para comunicarse con la API de Fútbol Prime
 */
object RetrofitClient {

    // 🔹 Cambia esta URL según tu configuración
    // Para emulador Android: http://10.0.2.2:8080/
    // Para dispositivo físico: http://TU_IP_LOCAL:8080/
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // Logging interceptor para debug
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP con configuraciones personalizadas
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Instancia de Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API Service
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}