package pt.isel.tests.drag.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pt.isel.tests.drag.repository.converters.Converters

@Database(entities = [Lobby::class, Player::class, Game::class, Round::class], version = 3)
@TypeConverters(Converters::class)
abstract class DragDB : RoomDatabase() {

    abstract fun lobbyDao() : LobbyDao

    abstract fun playerDao() : PlayerDao

    abstract fun gameDao() : GameDao

    abstract fun roundDao() : RoundDao
}