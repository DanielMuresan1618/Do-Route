package com.example.doroute.ui.view.map

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.doroute.R
import com.example.doroute.data.models.TaskModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.task.*


class CreateNewTask(
    private val location: LatLng
) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view: View = inflater.inflate(R.layout.task, null)
        builder.setView(view)
            .setTitle("Login")
            .setNegativeButton("cancel") { _, _ ->
                dialog?.cancel()
            }
            .setPositiveButton("submit") { _, _ ->
                val title = task_title.text.toString()
                val description = task_description.text.toString()
            }

        return builder.create()
    }

    interface ExampleDialogListener {
        fun addTask(task:TaskModel)
    }

}
