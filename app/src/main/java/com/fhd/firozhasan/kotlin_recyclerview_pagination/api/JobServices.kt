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

    /*
    *
    *
    *
    * what that getTopRatedMovies function means is
    * when calling this function i will send parameters
    * like api key, language and page and in return
    * THE REST API will RETURN PopularMovies object.
    * In addition that particular PopularMovies object could be
    * just object or contains list of inner object or something more.
    * THIS RETURNED OBJECT should be perfectly matched/aligned with the model class that
    * we have created
    * @Query means query String / parameter
    * @Field means body
    *
    *
    *
    *
    * */
}