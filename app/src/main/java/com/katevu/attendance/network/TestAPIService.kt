package com.katevu.attendance.network

import com.katevu.attendance.models.Auth
import com.katevu.attendance.models.User
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


private const val LOGIN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyAe3yRKq254ixkDxfqSvtvmsm9OXTOZx20"
private  const val BASE_URL = "https://identitytoolkit.googleapis.com"


/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface TestApiService {
    @POST(LOGIN_URL)
    suspend fun login(
        @Body user: User
    ): Response<Auth>

}



object TestApi {
    val retrofitService: TestApiService by lazy { retrofit.create(TestApiService::class.java) }
}