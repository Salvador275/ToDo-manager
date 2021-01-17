package com.example.todomanager

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.kotlinhell.TaskDto
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(private val taskContext: Context, list: ArrayList<TaskDto>) : ArrayAdapter<TaskDto?>(taskContext, 0, list as List<TaskDto?>) {
    private var itemList: List<TaskDto> = ArrayList()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem = convertView
        if (listItem == null) listItem = LayoutInflater.from(taskContext).inflate(R.layout.todo_item, parent, false)
        val currentTask = itemList[position]
        val name = listItem!!.findViewById<View>(R.id.textViewName) as TextView
        name.text = currentTask.title
        val description = listItem.findViewById<View>(R.id.textViewShortDescription) as TextView
        description.text = currentTask.description
        val sdf = SimpleDateFormat(Constants.DATE_FORMAT_JAVA, Locale.US)
        val deadline = listItem.findViewById<View>(R.id.textViewDeadline) as TextView
        deadline.text = sdf.format(currentTask.deadline)

        if (currentTask.title?.length!! > 15) {
            name.text = "${currentTask.title!!.substring(0, 14)}..."
        }

        if (currentTask.description?.length!! > 70) {
            description.text = "${currentTask.description!!.substring(0, 69)}..."
        }

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        val currentDate = calendar.time

        when {
            !currentTask.done -> {
                name.paintFlags = Paint.ANTI_ALIAS_FLAG
                description.paintFlags = Paint.ANTI_ALIAS_FLAG
                deadline.paintFlags = Paint.ANTI_ALIAS_FLAG
                name.setTextColor(Color.WHITE)
                description.setTextColor(Color.WHITE)

                when {
                    currentDate.after(currentTask.deadline) -> {
                        deadline.setTextColor(Color.RED)
                    }
                    currentDate.before(currentTask.deadline) -> {
                        deadline.setTextColor(Color.GREEN)
                    }
                    else -> {
                        deadline.setTextColor(Color.BLACK)
                    }
                }

            }
            currentTask.done -> {
                name.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                description.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                deadline.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

                name.setTextColor(Color.GRAY)
                description.setTextColor(Color.GRAY)
                deadline.setTextColor(Color.GRAY)
            }
        }

        return listItem
    }

    init {
        itemList = list
    }
}