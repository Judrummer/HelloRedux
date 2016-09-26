package com.github.judrummer.helloredux.fragment

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import com.github.judrummer.helloredux.R
import com.github.judrummer.helloredux.fragment.dummy.DummyContent
import com.github.judrummer.helloredux.model.Todo
import kotlinx.android.synthetic.main.fragment_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    var items: List<Todo> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var listener: TodoListFragment.OnListFragmentInteractionListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            tvItemId.text = items[position].id
            tvItemText.text = items[position].text
            setOnClickListener {
                listener?.onListFragmentInteraction(items[position])
            }
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        override fun toString(): String {
            return super.toString() + " '" + itemView.tvItemText.text + "'"
        }
    }
}
