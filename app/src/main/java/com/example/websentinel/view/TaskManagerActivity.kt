package com.example.websentinel.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.websentinel.R
import com.example.websentinel.data.DataSource
import com.example.websentinel.data.database.RoomDatabase
import com.example.websentinel.data.database.TaskDbStore
import com.example.websentinel.domain.TaskModel
import com.example.websentinel.viewmodel.TaskManagerViewModel
import com.example.websentinel.viewmodel.TaskManagerViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_task_manager.*
import java.util.*


class TaskManagerActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskRecyclerAdapter
    private lateinit var mTaskViewModel: TaskManagerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_manager)

        val factory = TaskManagerViewModelFactory(TaskDbStore(RoomDatabase.getDb(this)))
        findViewById<FloatingActionButton>(R.id.task_add).setOnClickListener{addTask()}

        mTaskViewModel = ViewModelProvider(this, factory).get(TaskManagerViewModel::class.java) //.of(this) is deprecated!!
        initRecyclerView()
    }

    private fun initRecyclerView(){

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@TaskManagerActivity)

        }
        mTaskViewModel.tasksLiveData.observe(this, Observer {
           taskAdapter= TaskRecyclerAdapter(it,this::delete)
            recycler_view.adapter = taskAdapter
        })

        mTaskViewModel.retrieveTasks()
    }

    private fun addTask(){
        mTaskViewModel.addTask("ceva", Date(2222222),"desc", "bla", "undone")
    }

    private fun delete(task: TaskModel){
        mTaskViewModel.removeTask(task)
    }


}
