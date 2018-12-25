package com.fhd.firozhasan.kotlin_recyclerview_pagination.ui

import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.fhd.firozhasan.kotlin_recyclerview_pagination.R
import com.fhd.firozhasan.kotlin_recyclerview_pagination.adapter.PopularMoviesAdapter
import com.fhd.firozhasan.kotlin_recyclerview_pagination.api.JobAPI
import com.fhd.firozhasan.kotlin_recyclerview_pagination.api.JobServices
import com.fhd.firozhasan.kotlin_recyclerview_pagination.models.PopularMovies
import com.fhd.firozhasan.kotlin_recyclerview_pagination.models.ResultsItem
import com.fhd.firozhasan.kotlin_recyclerview_pagination.utils.PaginationAdapterCallback
import com.fhd.firozhasan.kotlin_recyclerview_pagination.utils.PaginationScrollListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.error_layout.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeoutException

class MainActivity : AppCompatActivity(), PaginationAdapterCallback {

    override fun retryPageLoad() {
        loadNextPage()
    }

    private lateinit var apiclient: JobServices
    private lateinit var moviesadapter: PopularMoviesAdapter
    internal lateinit var linearLayoutManager: LinearLayoutManager

    private val TAG = "MainActivity"



    private val PAGE_START = 1
    private var isLoading = false
    private var isLastPage = false
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private var TOTAL_PAGES = 0
    private var currentPage = PAGE_START

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialize()

        moviesadapter = PopularMoviesAdapter(this)

        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        PopularMovies_RV.setLayoutManager(linearLayoutManager)
        PopularMovies_RV.setItemAnimator(DefaultItemAnimator())
        PopularMovies_RV.setAdapter(moviesadapter)
        PopularMovies_RV.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                this@MainActivity.isLoading = true
                currentPage += 1

                loadNextPage()
            }

            override fun getTotalPageCount(): Int {
                return TOTAL_PAGES
            }

            override fun isLastPage(): Boolean {
                return this@MainActivity.isLastPage
               // Log.d(TAG, this@MainActivity.isLastPage.toString())
            }

            override fun isLoading(): Boolean {
                return this@MainActivity.isLoading
              //  Log.d(TAG, "$isLoading")
            }

        })

        loadFirstPage()

    }


    private fun loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ")

        // To ensure list is visible when retry button in error view is clicked
        hideErrorView()

        callTopRatedMoviesApi().enqueue(object : Callback<PopularMovies> {
            override fun onResponse(call: Call<PopularMovies>, response: Response<PopularMovies>) {
                // Got data. Send it to adapter

                hideErrorView()

                val results = fetchResults(response)
                var totalpages = fetchTotalPages(response)
                TOTAL_PAGES = totalpages
                Log.d(TAG, "results: $results")
                Log.d(TAG, "totalpages: $totalpages")
                main_progress.setVisibility(View.GONE)
                moviesadapter.addAll(results)

                if (currentPage <= TOTAL_PAGES)
                    moviesadapter.addLoadingFooter()
                else
                    isLastPage = true
            }

            override fun onFailure(call: Call<PopularMovies>, t: Throwable) {
                t.printStackTrace()
                showErrorView(t)
            }
        })
    }

    private fun loadNextPage() {
        Log.d(TAG, "loadNextPage: $currentPage")

        callTopRatedMoviesApi().enqueue(object : Callback<PopularMovies> {
            override fun onResponse(call: Call<PopularMovies>, response: Response<PopularMovies>) {
                moviesadapter.removeLoadingFooter()
                isLoading = false

                val results = fetchResults(response)
                var totalpages = fetchTotalPages(response)
                Log.d(TAG, "results: $results")
                Log.d(TAG, "totalpages: $totalpages")
                moviesadapter.addAll(results)

                if (currentPage != TOTAL_PAGES)
                    moviesadapter.addLoadingFooter()
                else
                    isLastPage = true
            }

            override fun onFailure(call: Call<PopularMovies>, t: Throwable) {
                t.printStackTrace()
                moviesadapter.showRetry(true, fetchErrorMessage(t))
            }
        })
    }

    private fun hideErrorView() {
        if (error_layout.getVisibility() == View.VISIBLE) {
            error_layout.setVisibility(View.GONE)
            main_progress.setVisibility(View.VISIBLE)
        }
    }

    /**
     * Performs a Retrofit call to the top rated movies API.
     * Same API call for Pagination.
     * As [.currentPage] will be incremented automatically
     * by @[PaginationScrollListener] to load next page.
     */
    private fun callTopRatedMoviesApi(): Call<PopularMovies> {
        return apiclient.getPopularMovies(
            getString(R.string.my_api_key),
            "en_US",
            currentPage
        )
    }

    /**
     * @param response extracts List<[&gt;][Result] from response
     * @return
     */
    private fun fetchResults(response: Response<PopularMovies>): List<ResultsItem> {
        val popularMovies = response.body()
        return popularMovies!!.results!!
    }

    private fun fetchTotalPages(response: Response<PopularMovies>): Int {
        val popularMovies = response.body()
        return popularMovies!!.totalPages!!
    }

    fun initialize() {
        apiclient = JobAPI.client.create(JobServices::class.java)
        moviesadapter = PopularMoviesAdapter(this)
    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private fun fetchErrorMessage(throwable: Throwable): String {
        var errorMsg = resources.getString(R.string.error_msg_unknown)

        if (!isNetworkConnected()) {
            errorMsg = resources.getString(R.string.error_msg_no_internet)
        } else if (throwable is TimeoutException) {
            errorMsg = resources.getString(R.string.error_msg_timeout)
        }

        return errorMsg
    }

    /**
     * @param throwable required for [.fetchErrorMessage]
     * @return
     */
    private fun showErrorView(throwable: Throwable) {

        if (error_layout.getVisibility() == View.GONE) {
            error_layout.setVisibility(View.VISIBLE)
            main_progress.setVisibility(View.GONE)

            error_txt_cause.setText(fetchErrorMessage(throwable))
        }
    }
    // Helpers -------------------------------------------------------------------------------------



    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null
    }
}
