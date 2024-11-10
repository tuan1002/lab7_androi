package com.example.notes

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddNote : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        database = FirebaseDatabase.getInstance().getReference("notes")
    }

    fun them_moi(view: View) {
        val titleEditText = findViewById<EditText>(R.id.editTextTitle_addnote)
        val contentEditText = findViewById<EditText>(R.id.editTextContent__addnote)

        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()

        if (title.isNotEmpty() && content.isNotEmpty()) {
            addNote(title, content)
        } else {
            Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addNote(title: String, content: String) {
        val id = database.push().key ?: return

        // Lưu thời gian tạo ghi chú (thời gian hiện tại)
        val createdAt = System.currentTimeMillis()

        val note = Note(id, title, content, createdAt) // Tạo ghi chú với thời gian hiện tại
        database.child(id).setValue(note).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to add note", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
