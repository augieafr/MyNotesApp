package com.augie.mynotesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.augie.mynotesapp.databinding.ActivityMainBinding
import com.augie.mynotesapp.db.MappingHelper
import com.augie.mynotesapp.db.NoteHelper
import com.augie.mynotesapp.entity.Note
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title

        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.setHasFixedSize(true)
        adapter = NoteAdapter(this)
        binding.rvNotes.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, NoteAddUpdateActivity::class.java)
            startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_ADD)
        }

        if (savedInstanceState == null) {
            loadAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if (list != null) {
                adapter.listNote = list
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listNote)
    }

    private fun loadAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
            val noteHelper = NoteHelper.getInstance(applicationContext)
            noteHelper.open()

            val deferredNotes = async(Dispatchers.IO) {
                val cursor = noteHelper.querySelectAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }

            binding.progressBar.visibility = View.GONE
            val notes = deferredNotes.await()

            if (notes.size > 0) {
                adapter.listNote = notes
            } else {
                adapter.listNote = ArrayList()
                showSnackbarMessage("Tidak ada data saat ini")
            }

            noteHelper.close()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            when (requestCode) {
                NoteAddUpdateActivity.REQUEST_ADD -> if (resultCode == NoteAddUpdateActivity.RESULT_ADD) {
                    val note =
                        data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note

                    adapter.addItem(note)
                    binding.rvNotes.smoothScrollToPosition(adapter.itemCount - 1)

                    showSnackbarMessage("Satu item ditambahkan")
                }

                NoteAddUpdateActivity.REQUEST_UPDATE -> {
                    val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)
                    when (resultCode) {
                        NoteAddUpdateActivity.RESULT_UPDATE -> {
                            val note =
                                data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note

                            adapter.updateItem(position, note)
                            binding.rvNotes.smoothScrollToPosition(position)

                            showSnackbarMessage("${note.title} berhasil diubah")
                        }

                        NoteAddUpdateActivity.RESULT_DELETE -> {
                            adapter.deleteItem(position)
                            showSnackbarMessage("Note berhasil dihapus")
                        }
                    }
                }
            }
        }
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding.rvNotes, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val EXTRA_STATE = "extra_state"
    }
}