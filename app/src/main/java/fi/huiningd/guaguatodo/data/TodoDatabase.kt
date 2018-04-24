package fi.huiningd.guaguatodo.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Room
import android.content.Context


@Database(entities = [(TodoItem::class)], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoItemDao(): TodoItemDao

    @Volatile
    private var INSTANCE: TodoDatabase? = null

    fun getInstance(context: Context): TodoDatabase {
        if (INSTANCE == null) {
            synchronized(Database::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            TodoDatabase::class.java,
                            "todo-list.db")
                            .build()
                }
            }
        }
        return INSTANCE as TodoDatabase
    }
}
