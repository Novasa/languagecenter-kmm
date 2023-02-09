package com.novasa.languagecenter.data.database

import com.novasa.languagecenter.LanguageCenterDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DatabaseDriverFactory {

    actual fun createDriver(): SqlDriver = NativeSqliteDriver(LanguageCenterDatabase.Schema, "test.db")

}
