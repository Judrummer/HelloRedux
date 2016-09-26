package com.github.judrummer.helloredux.redux

import com.beyondeye.reduks.*

//fun todoListReducer(): ReducerImpl<TodoListState> = Reducer { state, action ->
//    when (action) {
//        is TodoAction.Init -> TodoListState()
//        is TodoAction.ResponseTodos -> state.copy(todos = action.todos)
//        is TodoAction.StartLoading -> state.copy(loading = true)
//        is TodoAction.FinishLoading -> state.copy(loading = false)
//        else -> state
//    }
//}
//
//fun realmMiddleware(): MiddlewareImpl<SimpleStore<AppState>> = Middleware { store, action, next ->
//    when (action) {
//
//    }
//}
