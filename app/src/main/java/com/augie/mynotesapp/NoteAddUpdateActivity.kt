package com.augie.mynotesapp

import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.augie.mynotesapp.databinding.ActivityNoteAddUpdateBinding
import com.augie.mynotesapp.db.DatabaseContract.NoteColumns.Companion.CONTENT_URI
import com.augie.mynotesapp.db.DatabaseContract.NoteColumns.Companion.DATE
import com.augie.mynotesapp.db.DatabaseContract.NoteColumns.Companion.DESCRIPTION
import com.augie.mynotesapp.db.DatabaseContract.NoteColumns.Companion.TITLE
import com.augie.mynotesapp.db.MappingHelper
import com.augie.mynotesapp.db.NoteHelper
import com.augie.mynotesapp.entity.Note
import java.text.SimpleDateFormat
import java.util.*

class NoteAddUpdateActivity : AppCompatActivity(), View.OnClickListener {

    private var isEdit = false
    private var note: Note? = null
    private var position: Int = 0
    private lateinit var noteHelper: NoteHelper
    private lateinit var binding: ActivityNoteAddUpdateBinding
    private lateinit var uriWithId: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteHelper = NoteHelper.getInstance(applicationContext)
        noteHelper.open()

        note = intent.getParcelableExtra(EXTRA_NOTE)

        if (note != null) {
            position = intent.getIntExtra(EXTRA_POSITION, 0)
            isEdit = true
        } else {
            note = Note()
        }

        val actionBarTitle: String
        val btnTitle: String

        if (isEdit) {
            actionBarTitle = "Ubah"
            btnTitle = "Update"

            uriWithId = Uri.parse(CONTENT_URI.toString() + "/" + note?.id)

            val cursor = contentResolver.query(uriWithId, null, null, null, null)
            if (cursor != null) {
                note = MappingHelper.mapCursorToObject(cursor)
                cursor.close()
            }

            note?.let {
                binding.edtTitle.setText(it.title)
                binding.edtDescription.setText(it.description)
            }

        } else {
            actionBarTitle = "Tambah"
            btnTitle = "Simpan"
        }

        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnSubmit.text = btnTitle
        binding.btnSubmit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_submit) {
            val title = binding.edtTitle.text.toString().trim()
            val description = binding.edtDescription.text.toString().trim()

            if (title.isEmpty()) {
                binding.edtTitle.error = "Field can not be blank"
                return
            }

            val values = ContentValues()
            values.put(TITLE, title)
            values.put(DESCRIPTION, description)

            if (isEdit) {
                contentResolver.update(uriWithId, values, null, null)
                Toast.makeText(
                    this@NoteAddUpdateActivity,
                    "Satu item berhasil diedit",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            } else {
                values.put(DATE, getCurrentDate())
                contentResolver.insert(CONTENT_URI, values)
                Toast.makeText(
                    this@NoteAddUpdateActivity,
                    "Satu item berhasil ditambahkan",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isEdit) {
            menuInflater.inflate(R.menu.menu_form, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> showAlertDialog(ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }

    private fun showAlertDialog(item: Int) {
        val isDialogClose = item == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String

        if (isDialogClose) {
            dialogTitle = "Batal"
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?"
        } else {
            dialogTitle = "Hapus Note"
            dialogMessage = "Apakah anda yakin menghapus item ini?"
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                if (isDialogClose) {
                    finish()
                } else {
                    contentResolver.delete(uriWithId, null, null)
                    Toast.makeText(
                        this@NoteAddUpdateActivity,
                        "Satu item berhasil dihapus",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.cancel()
            }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH::mm::ss", Locale.getDefault())
        val date = Date()

        return dateFormat.format(date)
    }

    companion object {
        const val EXTRA_POSITION = "extra_position"
        const val EXTRA_NOTE = "extra_note"

        const val REQUEST_ADD = 100
        const val RESULT_ADD = 101
        const val REQUEST_UPDATE = 200
        const val RESULT_UPDATE = 201
        const val RESULT_DELETE = 301
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }

}