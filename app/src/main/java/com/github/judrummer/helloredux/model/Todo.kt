package com.github.judrummer.helloredux.model

import io.realm.RealmModel
import io.realm.annotations.RealmClass
import java.util.*

@RealmClass
open class Todo(open var id: String = "",
                open var text: String = "",
                open var completed: Boolean = false,
                open var createdDate: Long = Date().time) : RealmModel {
    override fun hashCode(): Int = 1
    override fun equals(other: Any?): Boolean {
        if (other is Todo) {
            return id == other.id && text == other.text && completed == other.completed && createdDate == other.createdDate
        } else {
            return false
        }
    }

}

