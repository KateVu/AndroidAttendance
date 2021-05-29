package com.katevu.attendance.network

import com.katevu.attendance.data.model.ListActivities
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

private const val ACTIVITIES_URL = "https://mobile-attendance-recorder.herokuapp.com/api/v1/login"
private  const val BASE_URL = "https://mobile-attendance-recorder.herokuapp.com/"



/**
 * Build the Moshi object with Kotlin adapter factory that Retrofit will be using.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * The Retrofit object with the Moshi converter.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


interface GetActivities {
    @GET
    suspend fun getActivites(@Url fullUrl: String): Response<ListActivities>
}


/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object GetActivitiesApi {
    val retrofitService: GetActivities by lazy { retrofit.create(GetActivities::class.java) }
}