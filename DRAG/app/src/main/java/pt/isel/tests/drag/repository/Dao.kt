package pt.isel.tests.drag.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.ABORT

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

    @Update
    fun updatePlayers(lobby: Lobby)

}

@Dao
interface PlayerDao{
    @Delete
    fun delete(player: Player): Int

    @Insert(onConflict = ABORT)
    fun insert(player: Player)

    @Query("Select * from Player where lobbyId=:lobbyId and id=:playerId")
    fun get(lobbyId : String, playerId : String): LiveData<Player>

    @Query("Select * from Player where lobbyId = :lobbyId")
    fun getAllPlayers(lobbyId: String): LiveData<Player>
}