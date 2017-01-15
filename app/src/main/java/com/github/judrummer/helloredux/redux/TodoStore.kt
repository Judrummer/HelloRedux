package com.github.judrummer.helloredux.redux

import com.github.judrummer.helloredux.ObjectId
import com.github.judrummer.helloredux.model.Todo
import io.realm.Realm
import io.realm.RealmObject
import redux.api.Reducer
import redux.api.Store
import redux.api.enhancer.Middleware
import redux.applyMiddleware
import redux.createStore

sealed class TodoAction {
    object Init : TodoAction()
    class ResponseTodos(val todos: List<Todo>) : TodoAction()
    object StartLoading : TodoAction()
    object FinishLoading : TodoAction()

}

sealed class DbAction {
    object FetchTodoList : DbAction()
    class AddTodo(val text: String) : DbAction()
    class DeleteTodo(val id: String) : DbAction()
}

data class AppState(val todoListState: TodoListState = TodoListState())

data class TodoListState(val todos: List<Todo> = listOf(), val loading: Boolean = false)

fun appReducer() = Reducer<AppState> { state, action ->
    val todoListState = todoListReducer().reduce(state.todoListState, action)
    AppState(todoListState)
}

fun todoListReducer() = Reducer<TodoListState> { state, action ->
    when (action) {
        is TodoAction.Init -> TodoListState()
        is TodoAction.ResponseTodos -> state.copy(todos = action.todos)
        is TodoAction.StartLoading -> state.copy(loading = true)
        is TodoAction.FinishLoading -> state.copy(loading = false)
        else -> state
    }
}
//

fun realmMiddleware() = Middleware<AppState> { store, next, action ->
    if (action is DbAction) {
        val realm = Realm.getDefaultInstance()
        when (action) {
            is DbAction.FetchTodoList -> {
                val items = realm.where(Todo::class.java).findAll().map { realm.copyFromRealm(it) }.sortedByDescending { it.createdDate }
                next.dispatch(TodoAction.ResponseTodos(items))
            }
            is DbAction.AddTodo -> {
                realm.writeTransaction {
                    copyToRealm(Todo(ObjectId().toHexString(), action.text, false))
                }
                store.dispatch(DbAction.FetchTodoList)
            }
            is DbAction.DeleteTodo -> {
                realm.writeTransaction {
                    RealmObject.deleteFromRealm(where(Todo::class.java).equalTo("id", action.id).findFirst())
                }
                store.dispatch(DbAction.FetchTodoList)
            }
        }
        realm.close()
    } else {
        next.dispatch(action)
    }
}

val todoStore = createStore(appReducer(), AppState(), applyMiddleware(realmMiddleware()))


fun <T : Any> Realm.writeTransaction(init: Realm.() -> T): T {
    var t: T? = null

    executeTransaction {
        t = init(it)
    }

    return t!!
}