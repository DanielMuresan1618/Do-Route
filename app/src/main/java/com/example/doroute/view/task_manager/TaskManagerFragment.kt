package com.example.doroute.view.task_manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doroute.R
import com.example.doroute.data.database.RoomDatabase
import com.example.doroute.data.database.TaskDbStore
import com.example.doroute.domain.TaskModel
import com.example.doroute.viewmodel.TaskManagerViewModel
import com.example.doroute.viewmodel.TaskManagerViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_task_manager.*
import java.util.*
import java.util.UUID.randomUUID


class TaskManagerFragment : Fragment() {

    private val CHANNEL_ID: String = "channel1"
    private lateinit var notificationBuilder:NotificationCompat.Builder
    private lateinit var taskAdapter: TaskRecyclerAdapter
    private lateinit var mTaskViewModel: TaskManagerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_manager)


        createNotificationChannel()

        val factory = TaskManagerViewModelFactory(TaskDbStore(RoomDatabase.getDb(this)))
        findViewById<FloatingActionButton>(R.id.task_add).setOnClickListener{addTask()}

        mTaskViewModel = ViewModelProvider(this, factory).get(TaskManagerViewModel::class.java) //.of(this) is deprecated!!
        initRecyclerView()
    }


    private fun initRecyclerView(){

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@TaskManagerFragment)
        }
        mTaskViewModel.tasksLiveData.observe(this, Observer {
           taskAdapter=
               TaskRecyclerAdapter(
                   it,
                   this::delete,
                   this::update
               )
            recycler_view.adapter = taskAdapter
        })
        mTaskViewModel.retrieveTasks()
    }

    private fun update(task: TaskModel) {
        mTaskViewModel.updateTask(task)

    }

    private fun addTask(){
        mTaskViewModel.addTask(randomUUID().toString(),"ceva", Date(2222222),"desc", "bla", "undone")
    }

    private fun delete(task: TaskModel){
        val builder = AlertDialog.Builder(this)
        with(builder){
            setTitle("Warning!")
            setMessage("Are you sure you want to delete '${task.title}'?")
            //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            setPositiveButton(android.R.string.yes) { dialog, which ->

                mTaskViewModel.removeTask(task)
                Toast.makeText(applicationContext,
                    "The task '${task.title}' was deleted", Toast.LENGTH_SHORT).show()
                notifyNow()
            }
            setNegativeButton(android.R.string.no) {_,_->}
            show()
        }
    }


    private fun buildNotification(){
        /*val intent = Intent(this, GoogleMapsFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setContentTitle("My notification")
            .setContentText("Much longer text that cannot fit one line...")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Much longer text that cannot fit one line..."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

         */
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Channel name"
            val descriptionText = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun notifyNow(){
        buildNotification()
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(112, notificationBuilder.build())
        }
    }
}
