package com.github.judrummer.helloredux.redux

import android.util.Log
import com.beyondeye.reduks.*
import com.beyondeye.reduks.middlewares.applyMiddleware
import com.github.judrummer.helloredux.ObjectId
import com.github.judrummer.helloredux.model.Todo
import io.realm.Realm
import io.realm.RealmObject

fun <S> Store<S>.jx_subscribe(lambda: () -> Unit) = this.subscribe(StoreSubscriber<S> { lambda() })

sealed class TodoAction {
    object Init : TodoAction()
    object OpenRealm : TodoAction()
    object CloseRealm : TodoAction()
    object RequestFetchTodoList : TodoAction()
    class RequestAdd(val text: String) : TodoAction()
    class RequestDelete(val id: String) : TodoAction()
    class ResponseTodos(val todos: List<Todo>) : TodoAction()
    object StartLoading : TodoAction()
    object FinishLoading : TodoAction()

}

data class AppState(val todoListState: TodoListState = TodoListState())

data class TodoListState(val todos: List<Todo> = listOf(), val loading: Boolean = false)

//data class TodoDetailState(val todo: Todo)


fun appReducer(): ReducerImpl<AppState> = Reducer { state, action ->
//    val todoState = state.todoDetailState //todoListReducer().reduce(state.todoDetailState, action)
    val todoListState = todoListReducer().reduce(state.todoListState, action)
    AppState(todoListState)
}

fun todoListReducer(): ReducerImpl<TodoListState> = Reducer { state, action ->
    when (action) {
        is TodoAction.Init -> TodoListState()
        is TodoAction.ResponseTodos -> state.copy(todos = action.todos)
        is TodoAction.StartLoading -> state.copy(loading = true)
        is TodoAction.FinishLoading -> state.copy(loading = false)
        else -> state
    }
}

class RealmMiddleWare : IMiddleware<AppState> {

    private lateinit var realm: Realm

    override fun dispatch(store: Store<AppState>, next: (Any) -> Any, action: Any): Any {
        when (action) {
            is TodoAction.OpenRealm -> {
                Log.d("REALMDEBUG", "Open Realm")
                realm = Realm.getDefaultInstance()
            }
            is TodoAction.CloseRealm -> {
                Log.d("REALMDEBUG", "Close Realm")
                realm.close()
            }
            is TodoAction.RequestFetchTodoList -> {
                val items = realm.where(Todo::class.java).findAll().map { realm.copyFromRealm(it) }.sortedByDescending { it.createdDate }
                next(TodoAction.ResponseTodos(items))
            }
            is TodoAction.RequestAdd -> {
                realm.writeTransaction {
                    copyToRealm(Todo(ObjectId().toHexString(), action.text, false))
                }
                store.dispatch(TodoAction.RequestFetchTodoList)
            }
            is TodoAction.RequestDelete -> {
                realm.writeTransaction {
                    RealmObject.deleteFromRealm(where(Todo::class.java).equalTo("id", action.id).findFirst())
                }
                store.dispatch(TodoAction.RequestFetchTodoList)
            }
            else -> {
                next(action)
            }
        }
        return Unit
    }

}

val todoStore = SimpleStore(AppState(), appReducer()).applyMiddleware(RealmMiddleWare())


fun <T : Any> Realm.writeTransaction(init: Realm.() -> T): T {
    var t: T? = null

    executeTransaction {
        t = init(it)
    }

    return t!!
}