package com.example.doroute.ui.view.task_manager

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.doroute.R
import com.example.doroute.data.models.TaskModel
import kotlinx.android.synthetic.main.task.view.*


class TaskRecyclerAdapter(
    private val tasks: List<TaskModel>,
    private val onDeleteClick: (TaskModel) -> Unit,
    private val onUpdate: (TaskModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

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
        when(holder) {
            is TaskViewHolder -> {
                holder.bind(tasks[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }


    inner class TaskViewHolder(
        itemView:  View
    ): RecyclerView.ViewHolder(itemView){

        init {
            itemView.task_delete.setOnClickListener{onDeleteClick(tasks[adapterPosition])}
            itemView.task_title.setOnFocusChangeListener(this::onFocuseChange)
            itemView.task_title.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(s: Editable?) {
                    editingFinished=true
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

        }



        private fun onFocuseChange(view:View, hasFocus:Boolean){
            if (!hasFocus && editingFinished){
                editingFinished = false
                onUpdate(tasks[adapterPosition])
            }
        }

        fun bind(task: TaskModel){
            itemView.task_title.setText(task.title)
            itemView.task_description.setText(task.description)
            itemView.task_due_date.setText(task.dueDate.toString())
            itemView.task_location.text = task.locatioName
            itemView.task_status.text = task.statusId
        }
    }
}