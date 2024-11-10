package com.example.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

class NoteAdapter(
    private val noteList: List<Note>,
    private val onNoteClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View, private val onNoteClick: (Note) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val contentTextView: TextView = itemView.findViewById(R.id.textViewContent)

        fun bind(note: Note) {
            titleTextView.text = note.title
            contentTextView.text = note.content

            itemView.setOnClickListener { onNoteClick(note) }
        }

        private fun formatDate(timestamp: Long): String {
            val date = java.util.Date(timestamp)
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return format.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view, onNoteClick)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(noteList[position])
    }

    override fun getItemCount(): Int = noteList.size
}
