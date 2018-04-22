package fi.huiningd.guaguatodo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import fi.huiningd.guaguatodo.data.MyApp

import fi.huiningd.guaguatodo.data.TodoItem
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_todo_list.*

import kotlinx.android.synthetic.main.todo_list.*
import kotlin.collections.ArrayList

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [TodoItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class TodoListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var mTwoPane: Boolean = false
    private lateinit var mAdapter: TodoListRecyclerViewAdapter
    private var mTodoList: MutableList<TodoItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }*/

        if (todoitem_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true
        }

        //createDummyData()
        //retrieveData()
        setupRecyclerView(rv_todo_list)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        mAdapter = TodoListRecyclerViewAdapter(this, mTodoList, mTwoPane)
        recyclerView.adapter = mAdapter
    }

    private fun createDummyData() {
        createTodo("Take trash out")
        createTodo("Take trash out2")
        createTodo("Take trash out3")
        createTodo("Take trash out4")
        createTodo("Take trash out5")
    }

    private fun retrieveData() {
        MyApp.database?.todoItemDao()?.getAllTodoItems()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { todoList ->
                    mAdapter.updateList(todoList)
                }
    }

    private fun createTodo(title: String) {
        val todo = TodoItem(0, title, 12345, false, false)

        Single.fromCallable {
            MyApp.database?.todoItemDao()?.insert(todo)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }


}
