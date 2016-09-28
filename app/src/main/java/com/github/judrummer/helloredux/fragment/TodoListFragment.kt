package com.github.judrummer.helloredux.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondeye.reduks.IStoreSubscription
import com.github.judrummer.helloredux.R
import com.github.judrummer.helloredux.model.Todo
import com.github.judrummer.helloredux.redux.TodoAction
import com.github.judrummer.helloredux.redux.jx_subscribe
import com.github.judrummer.helloredux.redux.todoStore
import difflib.Delta
import difflib.DiffUtils
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_todo_list.*
import kotlinx.android.synthetic.main.item_todo.view.*

class TodoListFragment : Fragment() {

    lateinit var realm: Realm
    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null
    private var subscription: IStoreSubscription? = null
    private var adapter: TodoItemRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            columnCount = arguments.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater!!.inflate(R.layout.fragment_todo_list, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TodoItemRecyclerViewAdapter()
        rvTodoList.apply {
            layoutManager = if (columnCount <= 1) LinearLayoutManager(context) else GridLayoutManager(context, columnCount)
            adapter = this@TodoListFragment.adapter
        }
        realm = Realm.getDefaultInstance()
        subscription = todoStore.jx_subscribe {
            val state = todoStore.state.todoListState
            adapter?.items = state.todos
        }
        todoStore.dispatch(TodoAction.RequestFetchTodoList(realm))
    }

    override fun onDestroyView() {
        realm.close()
        subscription?.unsubscribe()
        super.onDestroyView()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context as OnListFragmentInteractionListener?
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: Todo)
    }

    companion object {

        private val ARG_COLUMN_COUNT = "column-count"

        @SuppressWarnings("unused")
        fun newInstance(columnCount: Int): TodoListFragment = TodoListFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_COLUMN_COUNT, columnCount)
            }
        }

    }


    inner class TodoItemRecyclerViewAdapter() : RecyclerView.Adapter<TodoListItemViewHolder>() {

        var items: List<Todo> = listOf()
            set(value) {
                val previous = field.toList()
                val current = value
                field = value
                val diffPatch = DiffUtils.diff(previous, current)
                if (diffPatch.deltas.size > 100) {
                    notifyDataSetChanged()
                } else if (diffPatch.deltas.size > 0) {
                    for (d in diffPatch.deltas) {
                        val original = d.original
                        val revised = d.revised
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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
            return TodoListItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: TodoListItemViewHolder, position: Int) {
            val item = items[position]
            holder.itemView.apply {
                tvItemId.visibility = View.GONE
                tvItemId.text = item.id
                tvItemText.text = item.text
                btItemDelete.setOnClickListener { todoStore.dispatch(TodoAction.RequestDelete(realm, item.id)) }
                setOnClickListener { listener?.onListFragmentInteraction(item) }
            }
        }

        override fun getItemCount(): Int = items.size

    }

    class TodoListItemViewHolder(view: View) : RecyclerView.ViewHolder(view)
}

