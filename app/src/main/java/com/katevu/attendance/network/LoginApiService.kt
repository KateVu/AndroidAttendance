package com.katevu.attendance.network

import com.katevu.attendance.data.model.LoggedInUser
import com.katevu.attendance.data.model.User
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

private const val LOGIN_URL = "https://mobile-attendance-recorder.herokuapp.com/api/v1/login"
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

/**
 * A public interface that exposes the [getAuth] method
 */
interface LoginApiService {
    /**
     * Returns a [List] of [MarsPhoto] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "photos" endpoint will be requested with the GET
     * HTTP method
     */
    @POST(LOGIN_URL)
    suspend fun login(
            @Body user: User
    ): Response<LoggedInUser>

}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object LoginApi {
    val retrofitService: LoginApiService by lazy { retrofit.create(LoginApiService::class.java) }
}