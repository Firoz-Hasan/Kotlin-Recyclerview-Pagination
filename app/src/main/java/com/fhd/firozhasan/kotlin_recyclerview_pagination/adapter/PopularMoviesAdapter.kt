package com.fhd.firozhasan.kotlin_recyclerview_pagination.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.fhd.firozhasan.kotlin_recyclerview_pagination.R
import com.fhd.firozhasan.kotlin_recyclerview_pagination.models.ResultsItem
import com.fhd.firozhasan.kotlin_recyclerview_pagination.utils.PaginationAdapterCallback
import kotlinx.android.synthetic.main.item_list.view.*
import kotlinx.android.synthetic.main.item_progress.view.*
import java.util.ArrayList


class PopularMoviesAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var movieResults: MutableList<ResultsItem>? = null
    private var retryPageLoad = false
    private var errorMsg: String? = null
    private var isLoadingAdded = false
    private var mCallback: PaginationAdapterCallback
    private val TAG = "PopularMoviesAdapter"

    init {
        this.mCallback = context as PaginationAdapterCallback
        movieResults = ArrayList<ResultsItem>() as MutableList<ResultsItem>?
    }
    companion object {

        // View Types
        private val ITEM = 0
        private val LOADING = 1

        private val BASE_URL_IMG = "https://image.tmdb.org/t/p/w300"
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(p0.context)

        when (p1) {
            PopularMoviesAdapter.ITEM -> {
                val viewItem = inflater.inflate(R.layout.item_list, p0, false)
                viewHolder = PopularMoviesViewHolder(viewItem)
            }
            PopularMoviesAdapter.LOADING -> {
                val viewLoading = inflater.inflate(R.layout.item_progress, p0, false)
                viewHolder = LoadingViewHolder(viewLoading)
            }
        }
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return if (movieResults == null) 0 else movieResults!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == movieResults!!.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val result = movieResults!![p1] // Movie
        when (getItemViewType(p1)) {

            PopularMoviesAdapter.ITEM -> {
                val popularmovieVH = p0 as PopularMoviesAdapter.PopularMoviesViewHolder

                popularmovieVH.movie_title.text = result?.title
                popularmovieVH.movie_desc.text = result?.overview
                popularmovieVH.movie_year.text = result?.releaseDate
                val poster_path = result?.posterPath
                Glide.with(context).load(BASE_URL_IMG+poster_path).into(popularmovieVH.poster)


            }

            PopularMoviesAdapter.LOADING -> {
                val loadingVH = p0 as PopularMoviesAdapter.LoadingViewHolder
                Log.d("retry", "$retryPageLoad" )
                if (retryPageLoad) {
                    loadingVH.mErrorLayout.visibility = View.VISIBLE
                    loadingVH.mProgressBar.visibility = View.GONE

                    loadingVH.mErrorTxt.text = if (errorMsg != null)
                        errorMsg
                    else
                        context.getString(R.string.error_msg_unknown)

                } else {
                    loadingVH.mErrorLayout.visibility = View.GONE
                    loadingVH.mProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }


    /*
        Helpers - Pagination
   _________________________________________________________________________________________________
    */

    fun add(r: ResultsItem) {
        movieResults!!.add(r)
        notifyItemInserted(movieResults!!.size - 1)
        Log.d(TAG, movieResults!!.size.toString() )
    }

    fun addAll(moveResults: List<ResultsItem>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun remove(r: ResultsItem?) {
        val position = movieResults!!.indexOf(r)
        Log.d(TAG, "remove= $position" )
        if (position > -1) {
            movieResults!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }


    fun addLoadingFooter() {
        isLoadingAdded = true
        Log.d(TAG, "$isLoadingAdded" )
        Log.d(TAG, "$isLoadingAdded" )
        add(ResultsItem())

    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = movieResults!!.size - 1
        val result = getItem(position)
        Log.d(TAG, "removeLoadingFooter= $position" )
        Log.d(TAG, "$result" )

        if (result != null) {
            movieResults!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): ResultsItem? {
        return movieResults!![position]
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    fun showRetry(show: Boolean, errorMsg: String?) {
        retryPageLoad = show
        notifyItemChanged(movieResults!!.size - 1)

        if (errorMsg != null) this.errorMsg = errorMsg
    }


    /*
   _________________________________________________________________________________________________
    */

    internal class PopularMoviesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val movie_year = itemView.movie_year_TV
        val movie_title = itemView.movie_title__TV
        val movie_desc = itemView.movie_desc__TV
        val poster = itemView.movie_poster_IMV
        val progressbar = itemView.movie_progress

    }

    internal class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(p0: View?) {

            when (p0?.getId()) {
                R.id.loadmore_retry, R.id.loadmore_errorlayout -> {
                    adapter?.showRetry(false, null)
                    adapter?.mCallback?.retryPageLoad()
                }
            }
        }


        val mProgressBar = itemView.loadmore_progress
        val mRetryBtn = itemView.loadmore_retry
        val mErrorTxt = itemView.loadmore_errortxt
        val mErrorLayout = itemView.loadmore_errorlayout
        private val adapter: PopularMoviesAdapter? = null
        private val loadVH: LoadingViewHolder? = null

        init {
            mRetryBtn.setOnClickListener(this)
            mErrorLayout.setOnClickListener(this)
        }

    }
}