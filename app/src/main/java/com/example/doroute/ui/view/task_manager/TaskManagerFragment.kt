package com.example.doroute.ui.view.task_manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doroute.R
import com.example.doroute.data.database.RoomDatabase
import com.example.doroute.data.domain.stores.TaskDbStore
import com.example.doroute.databinding.ActivityMainBinding
import com.example.doroute.databinding.FragmentTaskManagerBinding
import com.example.doroute.data.models.TaskModel
import com.example.doroute.ui.viewmodel.TaskViewModel
import com.example.doroute.ui.viewmodel.TaskViewModelFactory
import kotlinx.android.synthetic.main.fragment_task_manager.*
import viewLifecycle


class TaskManagerFragment : Fragment() {

    private val CHANNEL_ID: String = "channel1"
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var taskAdapter: TaskRecyclerAdapter
    private lateinit var viewModel: TaskViewModel
    private lateinit var binding: FragmentTaskManagerBinding
    private val main_binding by viewLifecycle { ActivityMainBinding.bind(requireView()) }


    // View initialization logic
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //View model
        val factory =
            TaskViewModelFactory(
                TaskDbStore(
                    RoomDatabase.getDb(
                        requireContext()
                    )
                )
            )
        viewModel = requireActivity().let {
            ViewModelProvider(
                this,
                factory
            ).get(TaskViewModel::class.java)
        } //.of(this) is deprecated

        //Recycler view
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.retrieveTasks()
    }

    private fun initRecyclerView() {

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@TaskManagerFragment.requireContext())
        }
        recycler_view.addItemDecoration(
            TaskItemDecoration(
                resources.getDimension(R.dimen.task_management_items_margin).toInt()
            )
        )
        viewModel.tasksLiveData.observe(viewLifecycleOwner, Observer {
            taskAdapter =
                TaskRecyclerAdapter(
                    requireActivity(),
                    it,
                    this::delete,
                    this::update
                )
            recycler_view.adapter = taskAdapter
        })
        recycler_view.isNestedScrollingEnabled = false
        viewModel.retrieveTasks()
    }

    private fun update(task: TaskModel) {
        viewModel.updateTask(task, requireContext())

    }

    private fun delete(task: TaskModel) {
        val builder = AlertDialog.Builder(this@TaskManagerFragment.requireContext())
        with(builder) {
            setTitle("Warning!")
            setMessage("Are you sure you want to delete '${task.title}'?")
            //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            setPositiveButton(android.R.string.yes) { _, _ ->

                viewModel.removeTask(task, requireContext())
                Toast.makeText(
                    this@TaskManagerFragment.requireContext(),
                    "The task '${task.title}' was deleted", Toast.LENGTH_SHORT
                ).show()

            }
            setNegativeButton(android.R.string.no) { _, _ -> }
            show()
        }
    }
}

