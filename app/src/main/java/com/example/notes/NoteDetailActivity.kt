package com.example.notes

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NoteDetailActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button // Khai báo nút xóa
    private lateinit var database: DatabaseReference
    private var noteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextContent = findViewById(R.id.editTextContent)
        buttonSave = findViewById(R.id.buttonSaveNote)
        buttonDelete = findViewById(R.id.buttonDeleteNote)

        database = FirebaseDatabase.getInstance().getReference("notes")
        noteId = intent.getStringExtra("NOTE_ID")

        if (noteId != null) {
            loadNoteDetails(noteId!!)
        }

        buttonSave.setOnClickListener {
            if (noteId == null) {
                addNote() // Thêm ghi chú mới
            } else {
                updateNote(noteId!!) // Cập nhật ghi chú
            }
        }

        buttonDelete.setOnClickListener {
            noteId?.let { id ->
                deleteNote(id) // Gọi hàm xóa ghi chú
            }
        }
    }

    private fun loadNoteDetails(noteId: String) {
        database.child(noteId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val note = snapshot.getValue(Note::class.java)
                note?.let {
                    editTextTitle.setText(it.title)
                    editTextContent.setText(it.content)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun addNote() {
        val title = editTextTitle.text.toString()
        val content = editTextContent.text.toString()
        val id = database.push().key ?: return

        // Lưu thời gian tạo ghi chú (thời gian hiện tại)
        val createdAt = System.currentTimeMillis() // Thời gian hiện tại

        val note = Note(id, title, content, createdAt) // Tạo ghi chú với thời gian hiện tại
        database.child(id).setValue(note).addOnCompleteListener {
            Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    private fun updateNote(noteId: String) {
        val title = editTextTitle.text.toString()
        val content = editTextContent.text.toString()

        val note = Note(noteId, title, content)
        database.child(noteId).setValue(note).addOnCompleteListener {
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun deleteNote(noteId: String) {
        database.child(noteId).removeValue().addOnCompleteListener {
            Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
            finish() // Quay lại màn hình chính sau khi xóa
        }
    }
}
