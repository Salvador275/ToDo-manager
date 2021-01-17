package com.example.todomanager

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.android.synthetic.main.activity_new_task.*
import java.time.format.DateTimeFormatter

class NewTask : AppCompatActivity() {
    lateinit var database: DatabaseReference
    @RequiresApi(Build.VERSION_CODES.O)
    val dateFormatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT, Locale.US)
    lateinit var isoDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)
        setTitle("Добавить")

        this.database = FirebaseDatabase.getInstance(Constants.DATABASE_URL).reference

        bindCalendarDialogToTextInput()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTask(view: View) {

        val newTaskTitle = TaskNameInput.text.toString()
        val newTaskDescription = DescriptionInput.text.toString()
        val newTaskDeadline = EndDateInput.text.toString()

        if (newTaskTitle == "" || newTaskDeadline == "") {
            val alert = AlertDialog.Builder(this).setPositiveButton("Ok") { d, id->d.cancel() }
            alert.setMessage("Введите обязательные поля").create()
            alert.show()
        }
        else {
            val task = Task.create()
            task.title = newTaskTitle
            task.description = newTaskDescription
            task.deadline = this.isoDate
            task.done = false

            val createdTask = this.database.child(Constants.FIREBASE_ITEM).push()
            task.objectId = createdTask.key
            createdTask.setValue(task)

            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }
    }

    fun bindCalendarDialogToTextInput() {
        val calendar = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val sdf = SimpleDateFormat(Constants.DATE_FORMAT, Locale.US)
            val isoSdf = SimpleDateFormat(Constants.ISO_FORMAT, Locale.US)
            EndDateInput.setText(sdf.format(calendar.time))
            this.isoDate = isoSdf.format(calendar.time)
        }

        EndDateInput.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
}