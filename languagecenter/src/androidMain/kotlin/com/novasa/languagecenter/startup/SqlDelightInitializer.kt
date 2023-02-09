package com.novasa.languagecenter.startup

import android.content.Context
import androidx.startup.Initializer
import com.novasa.languagecenter.data.database.DatabaseDriverFactory
import com.squareup.sqldelight.db.SqlDriver

class SqlDelightInitializer : Initializer<SqlDriver> {

    override fun create(context: Context): SqlDriver = DatabaseDriverFactory.initialize(context)

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}