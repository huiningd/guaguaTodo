package fi.huiningd.guaguatodo

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
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

        holder.todoTitleView.tag = item
        holder.bind(item, mListItemListener)

    }

    override fun getItemCount(): Int {
        return mTodoList.size
    }

    fun updateList(newList: List<TodoItem>) {
        Log.e(TAG, "updateList")
        notifyChanges(mTodoList, newList)
        mTodoList = newList.toMutableList()
    }

    private fun notifyChanges(oldList: List<TodoItem>, newList: List<TodoItem>) {

        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].uid == newList[newItemPosition].uid
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

            override fun getOldListSize() = oldList.size

            override fun getNewListSize() = newList.size
        })

        diff.dispatchUpdatesTo(this)
    }

    private fun toggleStarred(wasStarred: Boolean, star: ImageButton) {
        if (wasStarred) clearStarred(star)
        else markStarred(star)
    }

    private fun markStarred(star: ImageButton) {
        star.setBackgroundResource(0)
        star.setBackgroundResource(R.drawable.ic_star_yellow)
    }

    private fun clearStarred(star: ImageButton) {
        star.setBackgroundResource(0)
        star.setBackgroundResource(R.drawable.ic_star_gray)
    }

    private fun toggleTextStrikeThru(isChecked: Boolean, textView: TextView) {
        if (isChecked) strikeThruText(textView)
        else clearStrikeThru(textView)
    }

    private fun strikeThruText(textView: TextView) {
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    private fun clearStrikeThru(textView: TextView) {
        textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val todoTitleView: TextView = mView.todo_title
        private val checkBox: CheckBox = mView.check_box
        private val star: ImageButton = mView.image_button_star

        fun bind(todoItem: TodoItem, mListener: ListItemListener?) {
            // Set listener to null so that the immediate isChecked does not trigger the listener
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = todoItem.isDone
            if (todoItem.isDone) strikeThruText(todoTitleView)
            if (todoItem.isStarred) markStarred(star)
            else clearStarred(star)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                toggleTextStrikeThru(isChecked, todoTitleView)
                mListener?.toggleDone(isChecked, todoItem)
            }

            star.setOnClickListener({
                val previousState = todoItem.isStarred
                toggleStarred(previousState, it as ImageButton )
                mListener?.toggleStarred(!previousState, todoItem)
            })

            todoTitleView.text = todoItem.title
            todoTitleView.setOnClickListener(mDetailViewListener)
        }
    }

    companion object {
        const val TAG = "RecyclerViewAdapter"
    }

}