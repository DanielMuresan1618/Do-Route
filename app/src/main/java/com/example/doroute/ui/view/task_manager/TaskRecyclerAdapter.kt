package com.example.doroute.ui.view.task_manager

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.doroute.R
import com.example.doroute.data.models.TaskModel
import com.example.doroute.helpers.TaskStates
import kotlinx.android.synthetic.main.task.view.*
import java.util.*


class TaskRecyclerAdapter(
    private val context: Context,
    private val tasks: List<TaskModel>,
    private val onDeleteClick: (TaskModel) -> Unit,
    private val onUpdate: (TaskModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG: String = "AppDebug"
    private var editingFinished = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.task,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TaskViewHolder -> {
                holder.bind(tasks[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }


    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var now: Calendar = Calendar.getInstance(Locale.getDefault())

        init {
            itemView.task_delete.setOnClickListener { onDeleteClick(tasks[adapterPosition]) }
            itemView.task_title.setOnFocusChangeListener(this::onFocusChange)
            itemView.task_description.setOnFocusChangeListener(this::onFocusChange)
            itemView.task_description.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    editingFinished = true
                    tasks[adapterPosition].description = itemView.task_description.text.toString()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            itemView.task_title.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    editingFinished = true
                    tasks[adapterPosition].title = itemView.task_title.text.toString()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })
            itemView.task_due_date.setOnClickListener(this::setDueDate)
            itemView.taskCheckbox.setOnClickListener { _ ->
                tasks[adapterPosition].checkboxChecked = !tasks[adapterPosition].checkboxChecked
                onUpdate(tasks[adapterPosition]) //onUpdate created a lot of problems...
            }
        }

        fun bind(task: TaskModel) {
            itemView.task_title.setText(task.title)
            itemView.task_description.setText(task.description)
            itemView.task_due_date.text = task.dueDate.toString().substringBefore("GMT") //formatare dată
            itemView.taskCheckbox.isChecked = task.checkboxChecked

            when (task.status) {
                TaskStates.COMPLETE -> itemView.task_status.setImageResource(R.drawable.ic_done_green_24dp)
                TaskStates.OVERDUE -> itemView.task_status.setImageResource(R.drawable.ic_overdue_red_24dp)
                TaskStates.PENDING -> itemView.task_status.setImageResource(R.drawable.ic_pending_yellow_24dp)
            }
        }

        private fun setDueDate(itemView: View) {
            val selectedCalendar: Calendar =
                Calendar.getInstance() // variable to collect custom date and time
            val datePicker = DatePickerDialog(
                context,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    //since the listener runs only after the user finished to pick a date...
                    selectedCalendar.set(Calendar.YEAR, year)
                    selectedCalendar.set(Calendar.MONTH, month)
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val timePicker = TimePickerDialog(
                        context,
                        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                            //... I can simulate an async behavior by using only the flow logic...
                            selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            selectedCalendar.set(Calendar.MINUTE, minute)
                            selectedCalendar.set(Calendar.SECOND, 0)
                            selectedCalendar.set(Calendar.MILLISECOND, 0)
                            //...thus updating the UI correctly...
                            itemView.task_due_date.text = selectedCalendar.time.toString()
                            //this will update the db too
                            tasks[adapterPosition].dueDate = selectedCalendar.time
                            onUpdate(tasks[adapterPosition]) //onUpdate created a lot of problems...
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                    )
                    //...and without spoiling the UX
                    timePicker.show()
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        private fun onFocusChange(view: View, hasFocus: Boolean) {
            if (!hasFocus && editingFinished) { //prevents pointless updates
                editingFinished = false
                onUpdate(tasks[adapterPosition])
            }
        }
    }
}