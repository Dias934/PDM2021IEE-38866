package pt.isel.tests.drag

import android.app.Application
import androidx.room.Room
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import pt.isel.tests.drag.repository.Database
import pt.isel.tests.drag.repository.LocalRepository
import pt.isel.tests.drag.repository.RemoteRepository

class DragApp : Application() {

    private val localDatabase by lazy{
        Room.databaseBuilder(this, Database::class.java, "local_db")
                .fallbackToDestructiveMigration()
                .build()
    }

    private val firestoreDatabase by lazy{
        Firebase.firestore.apply {
            firestoreSettings = firestoreSettings {
                isPersistenceEnabled = false
            }
        }
    }

    val localRepository by lazy { LocalRepository(localDatabase) }

    val remoteRepository by lazy { RemoteRepository(firestoreDatabase)}
}

val Application.localRepository : LocalRepository
    get() = (this as DragApp).localRepository

val Application.remoteRepository : RemoteRepository
    get() = (this as DragApp).remoteRepository