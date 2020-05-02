package com.example.websentinel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.websentinel.data.DataSource
import com.example.websentinel.viewmodel.TaskManagerViewModel
import kotlinx.android.synthetic.main.activity_task_manager.*


class TaskManagerActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskRecyclerAdapter
    private lateinit var mTaskViewModel: TaskManagerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_manager)
        initRecyclerView()
        addDataSet()

        mTaskViewModel = ViewModelProvider(this).get(TaskManagerViewModel::class.java) //.of(this) is deprecated!!
        mTaskViewModel.tasksLiveData.observe(this, Observer {

        })


    }

    private fun addDataSet(){
        val data = DataSource.createDataSet()
        taskAdapter.submitList(data)
    }

    private fun initRecyclerView(){

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@TaskManagerActivity)
            taskAdapter = TaskRecyclerAdapter()
            adapter = taskAdapter
        }
    }
}
