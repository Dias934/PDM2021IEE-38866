package pt.isel.tests.drag.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pt.isel.tests.drag.repository.converters.Converters
import pt.isel.tests.drag.repository.entities.*

@Database(entities = [Lobby::class, LocalPlayer::class, Game::class, LocalRound::class], version = 2)
@TypeConverters(Converters::class)
abstract class DragDB : RoomDatabase() {

    abstract fun lobbyDao() : LobbyDao

    abstract fun playerDao() : PlayerDao

    abstract fun gameDao() : GameDao

    abstract fun roundDao() : RoundDao
}