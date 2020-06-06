package com.example.doroute.ui.view.map

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import com.example.doroute.R
import com.example.doroute.data.models.TaskModel
import com.example.doroute.helpers.TaskStates
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.task.*
import java.util.*
import java.util.UUID.randomUUID


class CreateNewTask : DialogFragment() {

    private var listener: CreateWizardListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.create_task_wizard, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG,"reached")
        val activity = requireActivity()
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            .setTitle("Login")
            .setNegativeButton("cancel") { _, _ ->
                dialog?.dismiss()
            }
            .setPositiveButton("submit") { _, _ ->
//                val title = task_title.text.toString()
//                val description = task_description.text.toString()
//                val dueDate = Date(task_due_date.text.toString())
//                var location: LatLng
//                requireArguments().let {
//                    val safeArgs = CreateNewTaskArgs.fromBundle(it)
//                    location = safeArgs.location
//                }
//               val task = TaskModel(randomUUID().toString(),title,description,dueDate, location, TaskStates.PENDING,false,false)
//                listener?.addTask(task)
            }

        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as CreateWizardListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString().toString() +
                        "must implement CreateWizardListener"
            )
        }
    }

    interface CreateWizardListener {
        fun addTask(task:TaskModel)
    }

    companion object{
        const val TAG = "CreateNewTask"
    }

}
