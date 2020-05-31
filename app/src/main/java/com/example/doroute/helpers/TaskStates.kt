package com.example.doroute.helpers

object TaskStates {
    const val COMPLETE = 15
    const val PENDING = 20
    const val OVERDUE = 30

    fun getStateForValue(value: Int):String {
        when(value){
            COMPLETE -> return "COMPLETE"
            PENDING -> return "PENDING"
            OVERDUE -> return "OVERDUE"
        }
        return ""
    }
}