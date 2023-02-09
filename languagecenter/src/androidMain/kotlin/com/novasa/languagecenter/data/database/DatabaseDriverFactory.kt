package com.novasa.languagecenter.data.database

import android.content.Context
import com.novasa.languagecenter.LanguageCenterDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory {

    companion object {
        private lateinit var driver: SqlDriver

        fun initialize(context: Context): SqlDriver = AndroidSqliteDriver(
            schema = LanguageCenterDatabase.Schema,
            context = context,
            name = DatabaseConstants.NAME
        ).also {
            driver = it
        }
    }

    actual fun createDriver(): SqlDriver = driver
}
