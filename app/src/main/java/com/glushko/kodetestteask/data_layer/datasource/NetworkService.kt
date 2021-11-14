package com.glushko.kodetestteask.data_layer.datasource

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NetworkService {

    private const val BASE_URL = "https://stoplight.io/mocks/kode-education/trainee-test/25143926/"

    fun makeNetworkServiceRxJava(): ApiService {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val original = chain.request()

            val request = original.newBuilder()
                .header("Content-Type", "application/json")
                .header("Prefer", "code=200")
                .method(original.method(), original.body())
                .build()

            chain.proceed(request)
        }
        val client = httpClient.build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build().create(ApiService::class.java)
    }

}