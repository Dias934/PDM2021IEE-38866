package pt.isel.tests.drag

import android.app.Application
import androidx.room.Room
import pt.isel.tests.drag.repository.Database
import pt.isel.tests.drag.repository.LocalRepository

class DragApp : Application() {

    private val localDatabase by lazy{
        Room.databaseBuilder(this, Database::class.java, "local_db")
                .fallbackToDestructiveMigration()
                .build()
    }

    val localRepository by lazy{ LocalRepository(localDatabase) }
}

val Application.localRepository : LocalRepository
    get() = (this as DragApp).localRepository