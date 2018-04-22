package fi.huiningd.guaguatodo.data

import android.app.Application
import android.arch.persistence.room.Room


// Singleton. TODO: use dagger
class MyApp : Application() {

    companion object {
        var database: Database? = null
    }

    override fun onCreate() {
        super.onCreate()
        MyApp.database = Room.databaseBuilder(
                this, Database::class.java, "todo-list-db").build()
    }
}