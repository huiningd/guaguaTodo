package fi.huiningd.guaguatodo.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


const val TABLE_NAME = "todo_items"

@Entity (tableName = TABLE_NAME)
data class TodoItem(
        @PrimaryKey(autoGenerate = true)
        val uid: Long,

        val title: String = "",
        val date: Long,
        val isDone : Boolean = false,
        val isStarred : Boolean = false,
        val description: String = ""
)
