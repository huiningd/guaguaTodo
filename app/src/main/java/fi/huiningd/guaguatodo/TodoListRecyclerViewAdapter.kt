package fi.huiningd.guaguatodo

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import fi.huiningd.guaguatodo.data.TodoItem
import kotlinx.android.synthetic.main.todo_list_content.view.*


class TodoListRecyclerViewAdapter(private val mParentActivity: TodoListActivity,
                                  private val mValues: List<TodoItem>,
                                  private val mTwoPane: Boolean) :
        RecyclerView.Adapter<TodoListRecyclerViewAdapter.ViewHolder>() {


    private val mOnClickListener: View.OnClickListener
    private var mTodoList: List<TodoItem> = ArrayList()

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as TodoItem
            if (mTwoPane) {
                val fragment = TodoItemDetailFragment().apply {
                    arguments = Bundle().apply {
                        putLong(TodoItemDetailFragment.ARG_ITEM_ID, item.uid)
                    }
                }
                mParentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.todoitem_detail_container, fragment)
                        .commit()
            } else {
                val intent = Intent(v.context, TodoItemDetailActivity::class.java).apply {
                    putExtra(TodoItemDetailFragment.ARG_ITEM_ID, item.uid)
                }
                v.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.todo_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mTodoTitleView.text = item.title

        with(holder.itemView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    fun updateList(list: List<TodoItem>) {
        mTodoList = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val mCheckBox: CheckBox = mView.check_box
        val mTodoTitleView: TextView = mView.todo_title
    }
}