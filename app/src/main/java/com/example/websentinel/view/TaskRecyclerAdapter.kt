package com.example.websentinel.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.websentinel.R
import com.example.websentinel.domain.TaskModel
import kotlinx.android.synthetic.main.task.view.*


class TaskRecyclerAdapter(
    private val tasks: List<TaskModel>,
    private val onDeleteClick: (TaskModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    private val TAG: String = "AppDebug"

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
        }

        fun bind(task: TaskModel){
            itemView.task_title.setText(task.title)
            itemView.task_description.setText(task.description)
            itemView.task_date_created.text = task.dateCreated.toString()
            itemView.task_due_date.setText(task.dueDate.toString())
            itemView.task_location.text = task.location
            itemView.task_status.text = task.status
        }
    }
}