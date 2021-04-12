package com.augie.mynotesapp

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.augie.mynotesapp.databinding.ItemNoteBinding
import com.augie.mynotesapp.entity.Note

class NoteAdapter(private val activity: Activity) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    var listNote = ArrayList<Note>()
        set(listNote) {
            this.listNote.clear()
            this.listNote.addAll(listNote)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(listNote[position])
    }

    override fun getItemCount(): Int = listNote.size

    fun addItem(note: Note) {
        this.listNote.add(note)
        notifyItemInserted(this.listNote.size - 1)
        Log.d("Results", "insert to list: ${this.listNote.size}")
    }

    fun updateItem(position: Int, note: Note) {
        this.listNote[position] = note
        notifyItemChanged(position, note)
    }

    fun deleteItem(position: Int) {
        this.listNote.removeAt(position)
        if (this.listNote.size > 0) {
            notifyItemRangeChanged(position, this.listNote.size)
        } else {
            notifyDataSetChanged()
        }
        Log.d("Results", "removeAt: $position")
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemNoteBinding.bind(itemView)
        fun bind(note: Note) {
            with(binding) {
                tvItemDate.text = note.date
                tvItemTitle.text = note.title
                tvItemDescription.text = note.description
                cvItemNote.setOnClickListener(
                    CustomOnItemClickListener(
                        adapterPosition,
                        object : CustomOnItemClickListener.OnItemClickCallback {
                            override fun onItemClicked(view: View, position: Int) {
                                val intent = Intent(activity, NoteAddUpdateActivity::class.java)
                                intent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION, position)
                                intent.putExtra(NoteAddUpdateActivity.EXTRA_NOTE, note)
                                activity.startActivity(intent)
                            }
                        })
                )
            }
        }

    }
}