package fi.huiningd.guaguatodo

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import fi.huiningd.guaguatodo.data.TodoItem
import kotlinx.android.synthetic.main.todo_item_content.view.*


class TodoListRecyclerViewAdapter(private val mParentActivity: TodoListActivity,
                                  private val mTwoPane: Boolean) :
        RecyclerView.Adapter<TodoListRecyclerViewAdapter.ViewHolder>() {

    interface ListItemListener {
        fun toggleDone(isDone: Boolean, item: TodoItem)
        fun toggleStarred(isStarred: Boolean, item: TodoItem)
    }

    private val TAG = "RecyclerViewAdapter"

    private var mListItemListener : ListItemListener? = null
    private val mDetailViewListener: View.OnClickListener
    private var mTodoList: MutableList<TodoItem> = ArrayList()

    init {
        mDetailViewListener = View.OnClickListener { v ->
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

        try {
            mListItemListener = mParentActivity
        } catch (e: ClassCastException) {
            throw ClassCastException(mParentActivity.toString()
                    + " must implement " + mListItemListener.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.todo_item_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mTodoList[position]

        with(holder.itemView) {
            tag = item
            holder.bind(item, mListItemListener)
        }
    }

    override fun getItemCount(): Int {
        return mTodoList.size
    }

    fun updateList(list: List<TodoItem>) {
        //Log.e(TAG, "updateList")
        mTodoList = list.toMutableList()
        notifyDataSetChanged() // TODO make comparison with the old list, only refresh single item
    }

    private fun toggleTextStrikeThru(isChecked: Boolean, textView: TextView) {
        if (isChecked) strikeThruText(textView)
        else
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

    private fun strikeThruText(textView: TextView) {
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    inner class ViewHolder(mView: View ) : RecyclerView.ViewHolder(mView) {
        private val checkBox: CheckBox = mView.check_box
        private val todoTitleView: TextView = mView.todo_title

        fun bind(todoItem: TodoItem, mListener: ListItemListener?) {
            // Set listener to null so that the immediate isChecked does not trigger the listener
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = todoItem.isDone
            if (todoItem.isDone) strikeThruText(todoTitleView)
            if (todoItem.isStarred) {
                // TODO
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                toggleTextStrikeThru(isChecked, todoTitleView)
                mListener?.toggleDone(isChecked, todoItem)
            }

            todoTitleView.text = todoItem.title
            todoTitleView.setOnClickListener(mDetailViewListener)
        }
    }

}