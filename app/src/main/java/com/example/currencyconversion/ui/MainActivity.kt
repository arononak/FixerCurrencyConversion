package com.example.currencyconversion.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconversion.viewmodel.MainViewModel
import com.example.currencyconversion.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val model = ViewModelProviders.of(this).get(MainViewModel::class.java)

        model.fixers.observe(this, Observer {
            val adapterItems = ArrayList<Any>()
            it.forEach { adapterItems.addAll(FixerAdapter.toAdapterItems(it)) }
            model.adapterItems.value = adapterItems
            showProgress(false)
        })

        model.adapterItems.observe(this, Observer {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context).apply {
                    orientation = LinearLayoutManager.VERTICAL
                }
                if (adapter == null) {
                    adapter = FixerAdapter(context, it)
                } else {
                    (adapter!! as FixerAdapter).apply {
                        items = it
                        notifyDataSetChanged()
                        recyclerView.scrollToBottom()
                    }
                }
            }
        })

        model.apiError.observe(this, Observer {
            recyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE
            errorView.text = it
        })

        if (model.shouldFirstLoad()) {
            model.loadFirstDay()
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (shouldLoadNextItem()) {
                    showAdapterProgress(true)
                    model.loadNextDay()
                }
            }
        })
    }

    private fun shouldLoadNextItem(): Boolean {
        return isVisibleLastAdapterElement() && !(recyclerView.adapter as FixerAdapter).isProgress()
    }

    private fun showProgress(isShow: Boolean) {
        progressBar.visibility = if(!isShow) View.GONE else View.VISIBLE
    }

    private fun showAdapterProgress(isShow: Boolean) {
        recyclerView.apply {
            if (adapter != null) {
                (adapter as FixerAdapter).showProgress(isShow)
            }
        }
    }

    private fun isVisibleLastAdapterElement(): Boolean {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val position = layoutManager.findLastCompletelyVisibleItemPosition()
        val numItems = recyclerView.adapter!!.itemCount
        return position >= numItems -1
    }

    private fun RecyclerView.scrollToBottom() {
        this.smoothScrollToPosition(this.adapter!!.itemCount - 2)
    }
}
