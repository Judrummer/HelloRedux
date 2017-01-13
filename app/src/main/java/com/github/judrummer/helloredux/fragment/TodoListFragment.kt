package com.github.judrummer.helloredux.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beyondeye.reduks.IStoreSubscription
import com.beyondeye.reduks.subscribe
import com.github.judrummer.helloredux.R
import com.github.judrummer.helloredux.model.Todo
import com.github.judrummer.helloredux.redux.DbAction
import com.github.judrummer.helloredux.redux.TodoAction
import com.github.judrummer.helloredux.redux.todoStore
import com.github.judrummer.jxadapter.JxAdapter
import com.github.judrummer.jxadapter.JxDiffUtil
import com.github.judrummer.jxadapter.JxViewHolder
import kotlinx.android.synthetic.main.fragment_todo_list.*
import kotlinx.android.synthetic.main.item_todo.view.*

class TodoListFragment : Fragment() {

    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null
    private var subscription: IStoreSubscription? = null
//    private var adapter: TodoItemRecyclerViewAdapter? = null
    val todoAdapter by lazy {
        JxAdapter(JxViewHolder<Todo>(R.layout.item_todo) { position, item ->
            itemView.apply {
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
        }, jxDiffUtil = object : JxDiffUtil() {
            override val areItemsTheSame: (Any, Any) -> Boolean =
                    { old, new ->
                        (old is Todo && new is Todo) && old.id == new.id
                    }

        })
    }

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
//        adapter = TodoItemRecyclerViewAdapter()
        rvTodoList.apply {
            layoutManager = if (columnCount <= 1) LinearLayoutManager(context) else GridLayoutManager(context, columnCount)
            adapter = todoAdapter//this@TodoListFragment.adapter
        }
    }

    override fun onStart() {
        super.onStart()
        subscription = todoStore.subscribe {
            val state = todoStore.state.todoListState
            Log.d("MYDEBUG", "State Change $state")
//            adapter?.items = state.todos
            todoAdapter.items = state.todos
        }
        todoStore.dispatch(DbAction.FetchTodoList)
    }

    override fun onStop() {
        subscription?.unsubscribe()
        super.onStop()
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
}


