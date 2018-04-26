package fi.huiningd.guaguatodo

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.DatePicker
import kotlinx.android.synthetic.main.activity_add_new_todo.*
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import fi.huiningd.guaguatodo.TodoListActivity.Companion.KEY_NEW_TODO_DATE
import fi.huiningd.guaguatodo.TodoListActivity.Companion.KEY_NEW_TODO_TITLE


class AddNewTodoActivity : AppCompatActivity() {

    private var mCalendar = Calendar.getInstance()

    private val mDatePickerListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        mCalendar.set(Calendar.YEAR, year)
        mCalendar.set(Calendar.MONTH, monthOfYear)
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateDateInView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_todo)

        setToday()
        setUpListeners()
    }

    private fun setUpListeners() {
        // TODO: user should not be able to select a date older than today
        button_calendar!!.setOnClickListener {
            DatePickerDialog(this@AddNewTodoActivity,
                    mDatePickerListener,
                    // pre-select to today's date
                    mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        button_ok.setOnClickListener {
            val text = new_todo_edit_text.text.toString()
            if (!TextUtils.isEmpty(text))
                sendBackSignResult(text, mCalendar.time)
            else
                error_text_view.setText(R.string.error_task_empty)
        }

        button_cancel.setOnClickListener {
            finish()
        }
    }

    private fun setToday() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        today_text_view!!.text = sdf.format(mCalendar.time)
    }

    private fun updateDateInView() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        today_text_view!!.text = sdf.format(mCalendar.time)
    }

    private fun sendBackSignResult(title: String, date: Date) {
        val intent = Intent()
        intent.putExtra(KEY_NEW_TODO_TITLE, title)
        intent.putExtra(KEY_NEW_TODO_DATE, date)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}