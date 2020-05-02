package com.example.websentinel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_task_manager.*

class TaskManagerActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_manager)
        initRecyclerView()
        addDataSet()
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
