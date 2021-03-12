package pt.isel.tests.drag.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pt.isel.tests.drag.repository.converters.Converters

@Database(entities = [Lobby::class, Player::class], version = 1)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

    abstract fun lobbyDao() : LobbyDao

    abstract fun playerDao() : PlayerDao
}