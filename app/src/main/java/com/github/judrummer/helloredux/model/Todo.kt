package com.github.judrummer.helloredux.model

import io.realm.RealmModel
import io.realm.annotations.RealmClass

@RealmClass
open class Todo(open var id: String = "",
                open var text: String = "",
                open var completed: Boolean = false) : RealmModel

