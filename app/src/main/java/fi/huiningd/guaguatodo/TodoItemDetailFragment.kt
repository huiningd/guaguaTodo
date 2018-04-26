package fi.huiningd.guaguatodo

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import fi.huiningd.guaguatodo.data.TodoItem
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_todo_detail.*
import kotlinx.android.synthetic.main.todo_item_detail.view.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * A fragment representing a single TodoItem detail screen.
 * This fragment is either contained in a [TodoListActivity]
 * in two-pane mode (on tablets) or a [TodoItemDetailActivity]
 * on handsets.
 *
 */
class TodoItemDetailFragment : Fragment() {

    private var mItem: TodoItem? = null
    private var mCalendar = Calendar.getInstance()
    private var mView: View? = null
    private val mDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var mDatabase = TodoApp.database
    private var mIsStarred = false

    private val mDatePickerListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                mCalendar.set(Calendar.YEAR, year)
                mCalendar.set(Calendar.MONTH, monthOfYear)
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

    private val mShowCalendarListener = View.OnClickListener { _ ->
        DatePickerDialog(context,
                mDatePickerListener,
                // pre-select to today TODO set to current value
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private val mDeleteTaskListener = View.OnClickListener { _ ->
        val dialog = AlertDialog.Builder(context!!).create()
        dialog.setMessage(getString(R.string.confirm_delete))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), { _, _ ->
            deleteItem()
            activity!!.finish()
        })
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), { dialogInterface, _ ->
            dialogInterface.cancel()
        })
        dialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uid = arguments!!.getLong(ARG_ITEM_ID)
        //Single.fromCallable { mDatabase?.todoItemDao()?.findItemById(uid) }
        mDatabase?.todoItemDao()?.findItemById(uid)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { item ->
                    mItem = item
                    displayCurrentData(mView!!)
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.todo_item_detail, todoitem_detail_container, false)
        setUpListeners(mView!!)
        return mView
    }

    private fun displayCurrentData(view: View) {
        view.todo_edit_text.setText(mItem?.title)
        view.edit_text_extra_info.setText(mItem?.description)
        view.date_text_view.text = mDateFormat.format(mItem?.date)
        view.check_task_done.isChecked = mItem?.isDone!!
        val star = view.star_icon
        mIsStarred = mItem?.isStarred!!
        if (mIsStarred) markStarred(star)
        else clearStarred(star)
    }

    private fun setUpListeners(view: View) {
        view.button_calendar.setOnClickListener(mShowCalendarListener)
        view.date_text_view.setOnClickListener(mShowCalendarListener)
        view.text_view_done.setOnClickListener({ _ -> view.check_task_done.isChecked = true })
        view.star_icon.setOnClickListener( { v -> toggleStarred(v as ImageButton)} )
        view.button_delete_task.setOnClickListener(mDeleteTaskListener)
        view.button_save.setOnClickListener { validateAndSave(view) }
        view.button_cancel.setOnClickListener { activity!!.finish() }
    }

    private fun validateAndSave(view: View) {
        val task = view.todo_edit_text.text.toString()
        if (TextUtils.isEmpty(task))
            view.error_text_view.setText(R.string.error_task_empty)
        val extraInfo = view.edit_text_extra_info.text.toString()
        if (TextUtils.isEmpty(extraInfo))
            mItem?.description = extraInfo

        mItem?.title = task
        mItem?.isDone = view.check_task_done.isChecked
        mItem?.isStarred = mIsStarred

        updateItem()
        activity!!.finish()
    }

    private fun deleteItem() {
        Single.fromCallable { mDatabase?.todoItemDao()?.deleteItem(mItem!!) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    private fun updateItem() {
        Single.fromCallable { mDatabase?.todoItemDao()?.updateItem(mItem!!) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    private fun updateDateInView() {
        mView!!.date_text_view!!.text = mDateFormat.format(mCalendar.time)
    }

    private fun toggleStarred(star: ImageButton) {
        if (mIsStarred) clearStarred(star)
        else markStarred(star)
    }

    private fun markStarred(star: ImageButton) {
        star.setBackgroundResource(R.drawable.ic_star_yellow)
        mIsStarred = !mIsStarred
    }

    private fun clearStarred(star: ImageButton) {
        star.setBackgroundResource(R.drawable.ic_star_gray)
        mIsStarred = !mIsStarred
    }

    companion object {
        const val ARG_ITEM_ID = "item_id"
    }
}
