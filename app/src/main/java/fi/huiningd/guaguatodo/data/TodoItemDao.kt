package fi.huiningd.guaguatodo.data

import android.arch.persistence.room.*
import io.reactivex.Flowable

@Dao
interface TodoItemDao {

    /**
     * Why use Flowable?
     * When there is no data in the todoDatabase and the query returns no rows,
     * the Flowable will not emit, neither onNext, nor onError.
     * When there is a user in the todoDatabase, the Flowable will trigger onNext.
     * Every time the data is updated, the Flowable object will emit
     * automatically, allowing you to update the UI based on the latest data.
     *
     * see https://medium.com/google-developers/room-rxjava-acb0cd4f3757
     */

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