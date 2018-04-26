package fi.huiningd.guaguatodo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View

import fi.huiningd.guaguatodo.data.TodoItem

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_todo_list.*

import kotlinx.android.synthetic.main.todo_list.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * An activity representing a list of TodoItems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [TodoItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class TodoListActivity : AppCompatActivity(), TodoListRecyclerViewAdapter.ListItemListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var mTwoPane: Boolean = false

    private var mDatabase = TodoApp.database

    private lateinit var mAdapter: TodoListRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        add_todo.setOnClickListener { view ->
            val intent = Intent( view.context, AddNewTodoActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_TODO)
        }

        if (todoitem_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true
        }

        loadTodoList()
        setupRecyclerView(rv_todo_list)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        mAdapter = TodoListRecyclerViewAdapter(this, mTwoPane)
        recyclerView.adapter = mAdapter
    }

    private fun loadTodoList() {
        Log.e(TAG, "loadTodoList")
        mDatabase?.todoItemDao()?.getAllTodoItems()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { todoList ->
                    mAdapter.updateList(todoList)
                    if (todoList.isNotEmpty()) {
                        empty_placeholder.visibility = View.GONE
                    } else {
                        empty_placeholder.visibility = View.VISIBLE
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ADD_TODO && resultCode == RESULT_OK) {
            val newTodoTitle = data?.getStringExtra(KEY_NEW_TODO_TITLE) as String
            val date = data.getSerializableExtra(KEY_NEW_TODO_DATE) as Date
            val newItem = TodoItem(0, newTodoTitle, date.time, false, false)
            saveNewItemToRoom(newItem)
        }
    }

    private fun saveNewItemToRoom(newItem: TodoItem) {
        Single.fromCallable { mDatabase?.todoItemDao()?.insert(newItem) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    override fun toggleDone(isDone: Boolean, item: TodoItem) {
        item.isDone = isDone
        Single.fromCallable { mDatabase?.todoItemDao()?.updateItem(item) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    override fun toggleStarred(isStarred: Boolean, item: TodoItem) {
        item.isStarred = isStarred
        Single.fromCallable { mDatabase?.todoItemDao()?.updateItem(item) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    companion object {
        const val TAG = "TodoListActivity"
        const val REQUEST_CODE_ADD_TODO = 1
        const val KEY_NEW_TODO_TITLE = "title"
        const val KEY_NEW_TODO_DATE = "date"
    }

}
