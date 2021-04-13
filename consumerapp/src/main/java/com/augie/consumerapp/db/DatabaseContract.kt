package com.augie.consumerapp.db

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {

    const val AUTHORITY = "com.augie.mynotesapp"
    const val SCHEME = "content"

    internal class NoteColumns : BaseColumns {
        companion object {
            const val _ID = "_id"
            const val TABLE_NAME = "note"
            const val TITLE = "title"
            const val DESCRIPTION = "description"
            const val DATE = "date"

            val CONTENT_URI: Uri = Uri.Builder().scheme((SCHEME))
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}