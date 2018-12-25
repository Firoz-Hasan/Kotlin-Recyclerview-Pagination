package com.fhd.firozhasan.kotlin_recyclerview_pagination.api
import com.fhd.firozhasan.kotlin_recyclerview_pagination.`interface`.Constant
import com.fhd.firozhasan.kotlin_recyclerview_pagination.models.PopularMovies
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface JobServices {

    @GET(Constant.POPULAR_MOVIES)
    fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") pageIndex: Int
    ): Call<PopularMovies>

}