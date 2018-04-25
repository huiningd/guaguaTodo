package fi.huiningd.guaguatodo.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

import java.util.Date

const val TABLE_NAME = "todo_items"

@Entity (tableName = TABLE_NAME)
data class TodoItem(
        @PrimaryKey(autoGenerate = true)
        val uid: Long,

        var title: String = "",
        var date: Long,
        var isDone : Boolean = false,
        var isStarred : Boolean = false,
        var description: String = ""
)
