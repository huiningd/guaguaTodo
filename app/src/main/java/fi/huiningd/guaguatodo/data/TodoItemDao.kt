package fi.huiningd.guaguatodo.data

import android.arch.persistence.room.*
import io.reactivex.Flowable

@Dao
interface TodoItemDao {

    @Query("SELECT * FROM $TABLE_NAME")
    fun getAllTodoItems(): Flowable<List<TodoItem>>

    @Insert
    fun insert(todoItem: TodoItem)

    @Query("select * from $TABLE_NAME where uid = :uid")
    fun findItemById(uid: Long): Flowable<TodoItem>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateItem(item: TodoItem)

    @Delete
    fun deleteItem(item: TodoItem)
}