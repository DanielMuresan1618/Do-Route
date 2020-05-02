package com.example.websentinel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.websentinel.domain.TaskModel
import kotlinx.android.synthetic.main.task.view.*

import kotlin.collections.ArrayList


class TaskRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    private val TAG: String = "AppDebug"

    private var items: List<TaskModel> = ArrayList()

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
                holder.bind(items.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(blogList: List<TaskModel>){
        items = blogList
    }

    class TaskViewHolder
    constructor(
        itemView:  View
    ): RecyclerView.ViewHolder(itemView){
        val taskTitle = itemView.task_title
        var taskDescription = itemView.task_description
        var taskDateCreated = itemView.task_date_created
        var taskDueDate = itemView.task_due_date
        var taskLocation= itemView.task_location
        var taskStatus= itemView.task_status

        fun bind(task: TaskModel){
            taskTitle.setText(task.title)
            taskDescription.setText(task.description)
            taskDateCreated.setText(task.dateCreated.toString())
            taskDueDate.setText(task.dueDate.toString())
            taskLocation.setText(task.location.toString())
            taskStatus.setText(task.status)
        }
    }
}