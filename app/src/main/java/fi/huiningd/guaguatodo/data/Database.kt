package fi.huiningd.guaguatodo.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = arrayOf(TodoItem::class), version = 1)
abstract class Database : RoomDatabase() {
    abstract fun todoItemDao(): TodoItemDao
}
