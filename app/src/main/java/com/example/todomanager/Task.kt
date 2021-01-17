package com.example.todomanager

class Task {
    companion object Factory {
        fun create(): Task = Task()
    }

    var objectId: String? = null
    var title: String? = null
    var description: String? = null
    var done: Boolean = false
    var deadline: String? = null
}