package com.fhd.firozhasan.kotlin_recyclerview_pagination.api
import com.fhd.firozhasan.kotlin_recyclerview_pagination.`interface`.Constant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JobAPI {

    companion object {

        private var retrofit: Retrofit? = null

        val client: Retrofit
            get() {
                if (retrofit == null) {
                    retrofit = Retrofit.Builder()
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl(Constant.BASE_URL)
                            .build()
                }
                return retrofit!!
            }
    }
}