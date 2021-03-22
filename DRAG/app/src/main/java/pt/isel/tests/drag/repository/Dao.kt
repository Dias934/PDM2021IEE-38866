package pt.isel.tests.drag.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.ABORT
import java.time.LocalDateTime

@Dao
interface LobbyDao{

    @Delete
    fun delete(lobby: Lobby): Int

    @Insert(onConflict = ABORT)
    fun insert(lobby: Lobby)

    @Query("Select * from Lobby where id=:id")
    fun get(id : String): LiveData<Lobby>

    @Query("Select * from Lobby")
    fun getAll(): LiveData<List<Lobby>>

    @Update
    fun updatePlayers(lobby: Lobby)

}

@Dao
interface PlayerDao{
    @Delete
    fun delete(player: Player): Int

    @Insert(onConflict = ABORT)
    fun insert(player: Player)

    @Insert(onConflict = ABORT)
    fun insertAll(players:MutableList<Player>)

    @Query("Select * from Player where lobbyId=:lobbyId and name=:playerName")
    fun get(lobbyId : String, playerName : String): LiveData<Player>

    @Query("Select * from Player where lobbyId = :lobbyId")
    fun getAllPlayers(lobbyId: String): LiveData<List<Player>>

    @Query("Update Player set name = :newPlayerName where name=:oldPlayerName")
    fun updatePlayerName(oldPlayerName: String, newPlayerName: String)
}

@Dao
interface GameDao{
    @Delete
    fun delete(game: Game): Int

    @Insert(onConflict = ABORT)
    fun insert(game: Game)

    @Query("Select * from Game where lobbyId=:lobbyId and date=:date")
    fun get(lobbyId : String, date : LocalDateTime): LiveData<Game>

    @Query("Select * from Game where lobbyId = :lobbyId")
    fun getAllGames(lobbyId: String): LiveData<List<Game>>
}

@Dao
interface RoundDao{
    @Delete
    fun delete(round: Round): Int

    @Insert(onConflict = ABORT)
    fun insert(round: Round)

    @Query("Select * from Round where lobbyId=:lobbyId and gameDate=:date and number=:number")
    fun get(lobbyId: String, date: LocalDateTime, number: Int): LiveData<Round>

    @Query("Select * from Round where lobbyId = :lobbyId and gameDate=:date")
    fun getAllRounds(lobbyId: String, date: LocalDateTime): LiveData<List<Round>>
}