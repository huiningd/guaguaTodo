package fi.huiningd.guaguatodo

import android.app.Application
import android.arch.persistence.room.Room
import fi.huiningd.guaguatodo.data.TodoDatabase

class TodoApp : Application() {

    companion object {
        var database: TodoDatabase? = null
    }

    override fun onCreate() {
        super.onCreate()
        TodoApp.database = Room.databaseBuilder(
                this,
                TodoDatabase::class.java,
                "todo-list.db")
                .build()
    }
}