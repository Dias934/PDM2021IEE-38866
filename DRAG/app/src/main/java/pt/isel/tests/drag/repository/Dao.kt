package pt.isel.tests.drag.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.Query

@Dao
interface LobbyDao{

    @Delete
    fun delete(lobby: Lobby): Int

    @Insert(onConflict = ABORT)
    fun insert(lobby: Lobby)

    @Query("Select * from Lobby where id=:id")
    fun get(id : String): LiveData<Lobby>

    @Query("Select * from Lobby")
    fun getAll(): LiveData<Lobby>

}

@Dao
interface PlayerDao{
    @Delete
    fun delete(lobby: Lobby): Int

    @Insert(onConflict = ABORT)
    fun insert(lobby: Lobby)

    @Query("Select * from Player where lobbyId=:lobbyId and id=:playerId")
    fun get(lobbyId : String, playerId : String): LiveData<Player>

    @Query("Select * from Player where lobbyId = :lobbyId")
    fun getAllPlayers(lobbyId: String): LiveData<Player>
}