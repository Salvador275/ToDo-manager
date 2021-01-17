package com.example.todomanager

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class TaskCard : AppCompatActivity() {
    lateinit var database: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDate(dateToConvert: LocalDate): Date? {
        return Date.from(
            dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_card)

        val taskTitle = intent.getStringExtra("taskTitle")
        val taskDescription = intent.getStringExtra("taskDescription")
        val taskDeadline = intent.getStringExtra("taskDeadline")

        val taskTitleView = findViewById<EditText>(R.id.showTaskTitle)
        val taskDescriptionView = findViewById<EditText>(R.id.showTaskDescription)
        val taskDeadlineView = findViewById<EditText>(R.id.showTaskDeadline)

        taskTitleView.setText(taskTitle)
        taskDescriptionView.setText(taskDescription)
        taskDeadlineView.setText(taskDeadline)

        val dateFormatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT)
        val deadlineDate = convertDate(LocalDate.parse(taskDeadline, dateFormatter))

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        val currentDate = calendar.time

        when {
            currentDate.after(deadlineDate) -> {
                taskDeadlineView.setTextColor(Color.RED)
            }
            currentDate.before(deadlineDate) -> {
                taskDeadlineView.setTextColor(Color.GREEN)
            }
            else -> {
                taskDeadlineView.setTextColor(Color.BLACK)
            }
        }

        this.database = FirebaseDatabase.getInstance(Constants.DATABASE_URL).reference
        bindExecuteTaskButton()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun modifyItemState(itemObjectId: String, isDone: Boolean) {
        val itemReference = this.database.child(Constants.FIREBASE_ITEM).child(itemObjectId)
        itemReference.child("done").setValue(isDone);
    }

    fun bindExecuteTaskButton() {
        val executeTaskButton = findViewById<Button>(R.id.executeTaskButton)
        executeTaskButton.setOnClickListener {
            intent.getStringExtra("taskId")?.let { it1 -> modifyItemState(it1, true) }
            val alert = AlertDialog.Builder(this).setPositiveButton("Ok") { dialogInterface: DialogInterface, i: Int ->
                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent)
            }
            alert.setMessage("Миссия выполнена!").create()
            alert.show()
        }
    }
}