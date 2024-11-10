package com.example.notes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonAdd: Button
    private lateinit var database: DatabaseReference
    private lateinit var noteList: MutableList<Note>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        buttonAdd = findViewById(R.id.buttonAdd)

        database = FirebaseDatabase.getInstance().getReference("notes")
        noteList = mutableListOf()

        recyclerView.layoutManager = LinearLayoutManager(this)

        buttonAdd.setOnClickListener {
            val intent = Intent(this, AddNote::class.java)
            startActivity(intent)
        }

        loadNotes()

        // Thêm chức năng vuốt để xóa ghi chú
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val noteToDelete = noteList[position]
                deleteNote(noteToDelete.id) // Xóa ghi chú từ Firebase
                noteList.removeAt(position) // Xóa ghi chú khỏi danh sách
                recyclerView.adapter?.notifyItemRemoved(position) // Cập nhật RecyclerView
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView) // Gắn ItemTouchHelper vào RecyclerView
    }

    private fun loadNotes() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                noteList.clear()
                for (data in snapshot.children) {
                    val note = data.getValue(Note::class.java)
                    note?.let { noteList.add(it) }
                }
                recyclerView.adapter = NoteAdapter(noteList) { note ->
                    val intent = Intent(this@MainActivity, NoteDetailActivity::class.java)
                    intent.putExtra("NOTE_ID", note.id)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun deleteNote(noteId: String) {
        database.child(noteId).removeValue() // Xóa ghi chú khỏi Firebase
    }
}

