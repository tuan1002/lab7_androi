package com.example.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class NoteViewModel : ViewModel() {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("notes")
    private val _allNotes = MutableLiveData<List<Note>>()
    val allNotes: LiveData<List<Note>> get() = _allNotes

    fun add(note: Note) {
        val noteId = database.push().key ?: return
        note.id = noteId
        database.child(noteId).setValue(note)
    }

    fun update(note: Note) {
        database.child(note.id).setValue(note)
    }

    fun delete(noteId: String) {
        database.child(noteId).removeValue()
    }

    fun getNoteById(noteId: String): MutableLiveData<Note?> {
        val noteLiveData = MutableLiveData<Note?>()
        database.child(noteId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val note = snapshot.getValue(Note::class.java)
                noteLiveData.value = note
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
        return noteLiveData
    }
}
