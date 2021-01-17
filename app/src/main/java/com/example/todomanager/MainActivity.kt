package com.example.todomanager

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import androidx.annotation.RequiresApi
import com.example.kotlinhell.TaskDto
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    lateinit var adapter: TaskAdapter
    private var listViewItems: ListView? = null
    lateinit var database: DatabaseReference
    var mappedItemList: ArrayList<TaskDto> = ArrayList()
    @RequiresApi(Build.VERSION_CODES.O)

    var itemListener: ValueEventListener = object : ValueEventListener {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            addDataToList(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.database = FirebaseDatabase.getInstance(Constants.DATABASE_URL).reference
        this.adapter = TaskAdapter(this, this.mappedItemList)
        this.listViewItems = findViewById(R.id.lvItems)
        this.listViewItems!!.adapter = this.adapter

        this.database.orderByKey().addListenerForSingleValueEvent(itemListener)

        val sdf = SimpleDateFormat(Constants.DATE_FORMAT, Locale.US)
        this.listViewItems!!.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, TaskCard::class.java)
            val selectedTask = this.mappedItemList[position]
            if (!selectedTask.done) {
                intent.putExtra("taskTitle", selectedTask.title)
                intent.putExtra("taskDescription", selectedTask.description)
                intent.putExtra("taskDeadline", sdf.format(selectedTask.deadline))
                intent.putExtra("taskId", selectedTask.objectId)
                this.startActivity(intent)
            }
        }
    }

    fun createNewRecord(view: View){
        val intent = Intent(this, NewTask::class.java)
        startActivity(intent)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun addDataToList(dataSnapshot: DataSnapshot) {
        val items = dataSnapshot.children.iterator()
        //Check if current database contains any collection
        if (items.hasNext()) {
            val toDoListindex = items.next()
            val itemsIterator = toDoListindex.children.iterator()

            //check if the collection has any to do items or not
            while (itemsIterator.hasNext()) {
                //get current item
                val currentItem = itemsIterator.next()
                val todoItem = Task.create()
                //get current data in a map
                val map = currentItem.getValue() as HashMap<*, *>
                //key will return Firebase ID
                todoItem.objectId = currentItem.key
                todoItem.title = map.get("title") as String?
                todoItem.description = map.get("description") as String?
                todoItem.deadline = map.get("deadline") as String
                todoItem.done = map.get("done") as Boolean
                this.mappedItemList.add(TaskDto(todoItem))
            }
        }
        Collections.sort(
            this.mappedItemList,
            Comparator.comparing(TaskDto::done)
                .thenComparing(TaskDto::deadline)
        )
        //alert adapter that has changed
        adapter.notifyDataSetChanged()
    }

    fun modifyItemState(itemObjectId: String, isDone: Boolean) {
        val itemReference = this.database.child(Constants.FIREBASE_ITEM).child(itemObjectId)
        itemReference.child("done").setValue(isDone);
    }

    //delete an item
    fun onItemDelete(itemObjectId: String) {
        //get child reference in database via the ObjectID
        val itemReference = this.database.child(Constants.FIREBASE_ITEM).child(itemObjectId)
        //deletion can be done via removeValue() method
        itemReference.removeValue()
    }
}