package com.github.judrummer.helloredux

import android.app.Application
import com.github.judrummer.helloredux.redux.TodoAction
import com.github.judrummer.helloredux.redux.todoStore
import io.realm.Realm
import io.realm.RealmConfiguration

class TodoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.setDefaultConfiguration(RealmConfiguration.Builder(this).name("helloredux.realm").schemaVersion(1).deleteRealmIfMigrationNeeded().build())
        todoStore.dispatch(TodoAction.OpenRealm)
    }

    override fun onTerminate() {
        todoStore.dispatch(TodoAction.CloseRealm)
        super.onTerminate()
    }
}

