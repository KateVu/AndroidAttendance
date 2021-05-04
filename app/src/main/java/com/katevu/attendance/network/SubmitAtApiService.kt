package com.katevu.attendance.network

import com.katevu.attendance.data.model.Attendance
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

//    val url: String = 'https://flutter-update.firebaseio.com/orders/$userId.json?auth=$authToken';

private const val LOGIN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyAe3yRKq254ixkDxfqSvtvmsm9OXTOZx20"
private  const val BASE_URL = "https://recordattendance-1fa08-default-rtdb.firebaseio.com/"



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


interface SubmitAtApiService {
    @POST
    suspend fun submitAttendance(@Url fullUrl: String,
            @Body attendance: Attendance
    ): Response<String>

}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object SubmitAtApi {
    val retrofitService: SubmitAtApiService by lazy { retrofit.create(SubmitAtApiService::class.java) }
}