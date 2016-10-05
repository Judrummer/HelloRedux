package com.github.judrummer.helloredux.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast

import com.github.judrummer.helloredux.R
import com.github.judrummer.helloredux.redux.DbAction
import com.github.judrummer.helloredux.redux.TodoAction
import com.github.judrummer.helloredux.redux.todoStore
import kotlinx.android.synthetic.main.fragment_todo_add.*

class TodoAddFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {

        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater!!.inflate(R.layout.fragment_todo_add, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etAddTodo.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addTodo()
                true
            } else {
                false
            }
        }
        btAddTodo.setOnClickListener {
            addTodo()
        }
    }

    fun addTodo() {
        if (etAddTodo.text.toString().isBlank()) {
            Toast.makeText(context, "Please Type Text", Toast.LENGTH_SHORT).show()
        } else {
            todoStore.dispatch(DbAction.AddTodo(etAddTodo.text.toString()))
            etAddTodo.setText("")
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {

        fun newInstance(): TodoAddFragment {
            return TodoAddFragment()
        }
    }


}// Required empty public constructor
