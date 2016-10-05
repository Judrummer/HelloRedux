package com.github.judrummer.helloredux.fragment

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.judrummer.helloredux.R
import com.github.judrummer.helloredux.model.Todo
import com.github.judrummer.helloredux.redux.DbAction
import com.github.judrummer.helloredux.redux.TodoAction
import com.github.judrummer.helloredux.redux.todoStore
import difflib.Delta
import difflib.DiffUtils
import kotlinx.android.synthetic.main.item_todo.view.*

class TodoItemRecyclerViewAdapter : RecyclerView.Adapter<TodoItemRecyclerViewAdapter.ViewHolder>() {

    var items: List<Todo> = listOf()
        set(value) {
            val previous = field.toList()
            val current = value
            field = value
//            notifyDataSetChanged()

            //calculate diff
            val diffPatch = DiffUtils.diff(previous, current)
            Log.d("ADAPTER", "diffPatch.deltas.size ${diffPatch.deltas.size}")
            if (diffPatch.deltas.size > 100) {
                notifyDataSetChanged()
            } else if (diffPatch.deltas.size > 0) {
                for (d in diffPatch.deltas) {
                    val original = d.original
                    val revised = d.revised
                    Log.d("ADAPTER", "${revised.position} ${revised.size()} ${original.size()}")
                    when (d.type) {
                        Delta.TYPE.DELETE -> {
                            notifyItemRangeRemoved(revised.position, original.size())
                        }
                        Delta.TYPE.CHANGE -> {
                            notifyItemRangeChanged(revised.position, revised.size())
                        }
                        Delta.TYPE.INSERT -> {
                            notifyItemRangeInserted(revised.position, revised.size())
                        }
                        else -> {
                        }
                    }
                }
            } else {
                // no change
            }

        }

    var listener: TodoListFragment.OnListFragmentInteractionListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Log.d("onBindViewHolder", "item[$position] -> ${item.text}")
        holder.itemView.apply {
            tvItemId.visibility = View.GONE
            tvItemId.text = item.id
            tvItemText.text = item.text
            btItemDelete.setOnClickListener {
                todoStore.dispatch(DbAction.DeleteTodo(item.id))
            }
            setOnClickListener {
                listener?.onListFragmentInteraction(item)
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
