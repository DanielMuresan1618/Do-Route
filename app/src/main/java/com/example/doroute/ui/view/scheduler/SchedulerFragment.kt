package com.example.doroute.ui.view.scheduler

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.doroute.R
import com.example.doroute.data.database.RoomDatabase
import com.example.doroute.data.domain.stores.TaskDbStore
import com.example.doroute.ui.viewmodel.TaskViewModel
import com.example.doroute.ui.viewmodel.TaskViewModelFactory

class SchedulerFragment : Fragment() {

    companion object {
        fun newInstance() = SchedulerFragment()
    }

    private lateinit var viewModel: TaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_scheduler, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val factory =
            TaskViewModelFactory(
                TaskDbStore(
                    RoomDatabase.getDb(
                        this.requireContext()
                    )
                )
            )
        viewModel = ViewModelProvider(this,factory).get(TaskViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
