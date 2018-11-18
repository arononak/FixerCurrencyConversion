package com.example.currencyconversion.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconversion.R
import com.example.currencyconversion.api.FixerObject
import kotlinx.android.synthetic.main.item_currency_header.view.*
import kotlinx.android.synthetic.main.item_currency_item.view.*


class FixerAdapter(private val context: Context, var items : ArrayList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        data class Header(val date: String)
        data class Item(val date: String, val rate: String)
        object Progress

        const val TYPE_HEADER = 1
        const val TYPE_ITEM = 2
        const val TYPE_PROGRESS = 3

        private class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title = view.text_title!!
        }

        private class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title = view.text_item!!
        }

        private class ProgressViewHolder(view: View) : RecyclerView.ViewHolder(view)

        fun toAdapterItems(fixer: FixerObject) : ArrayList<Any> {
            val items = arrayListOf<Any>(Header(fixer.date))
            fixer.rates.entrySet().iterator().forEach {
                items.add(Item(fixer.date, it.key + " " + it.value))
            }
            return items
        }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = when (items[position]) {
        is Header   -> TYPE_HEADER
        is Item     -> TYPE_ITEM
        is Progress -> TYPE_PROGRESS
        else        -> 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        TYPE_HEADER   -> HeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_currency_header, parent, false))
        TYPE_ITEM     -> ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_currency_item, parent, false))
        TYPE_PROGRESS -> ProgressViewHolder(LayoutInflater.from(context).inflate(R.layout.item_currency_progress, parent, false))
        else          -> throw IllegalStateException("Adapter overflow")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.title.text = (items[position] as Header).date
            is ItemViewHolder -> { holder.apply {
                    val item = items[position] as Item
                    title.text = item.rate
                    itemView.setOnClickListener {
                        context.startActivity(DetailsActivity.newIntent(context, item.date, item.rate))
                    }
                }
            }
        }
    }

    fun showProgress(isShow: Boolean) {
        items.apply { if (isShow) add(Progress) else remove(Progress) }
        notifyDataSetChanged()
    }

    fun isProgress(): Boolean = items.last() is Progress
}
