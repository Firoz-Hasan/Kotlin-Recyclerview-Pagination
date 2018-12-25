package com.fhd.firozhasan.kotlin_recyclerview_pagination.models

import com.google.gson.annotations.SerializedName

data class PopularMovies(@SerializedName("page")
                         val page: Int = 0,
                         @SerializedName("total_pages")
                         val totalPages: Int = 0,
                         @SerializedName("results")
                         val results: List<ResultsItem>?,
                         @SerializedName("total_results")
                         val totalResults: Int = 0)