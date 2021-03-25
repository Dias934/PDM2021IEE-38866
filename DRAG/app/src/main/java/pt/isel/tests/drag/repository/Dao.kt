package pt.isel.tests.drag.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.ABORT
import pt.isel.tests.drag.repository.entities.*
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

    @Query("Update Lobby set state = :state where id=:id")
    fun updateLobbyState(id: String, state: LobbyState)
}

@Dao
interface PlayerDao{
    @Delete
    fun delete(player: LocalPlayer): Int

    @Insert(onConflict = ABORT)
    fun insert(player: LocalPlayer)

    @Insert(onConflict = ABORT)
    fun insertAll(players:MutableList<LocalPlayer>)

    @Query("Select * from Player where lobbyId=:lobbyId and name=:playerName")
    fun get(lobbyId : String, playerName : String): LiveData<LocalPlayer>

    @Query("Select * from Player where lobbyId = :lobbyId")
    fun getAllPlayers(lobbyId: String): LiveData<List<LocalPlayer>>

    @Query("Update Player set name = :newPlayerName where name=:oldPlayerName")
    fun updatePlayerName(oldPlayerName: String, newPlayerName: String)
}

@Dao
interface GameDao{
    @Delete
    fun delete(localGame: Game): Int

    @Insert(onConflict = ABORT)
    fun insert(localGame: Game)

    @Query("Select * from Game where lobbyId=:lobbyId and date=:date")
    fun get(lobbyId : String, date : LocalDateTime): LiveData<Game>

    @Query("Select * from Game where lobbyId = :lobbyId")
    fun getAllGames(lobbyId: String): LiveData<List<Game>>
}

@Dao
interface RoundDao{
    @Delete
    fun delete(localRound: LocalRound): Int

    @Insert(onConflict = ABORT)
    fun insert(localRound: LocalRound)

    @Query("Select * from Round where lobbyId=:lobbyId and gameId=:gameId and number=:number")
    fun get(lobbyId: String, gameId: String, number: Int): LiveData<LocalRound>

    @Query("Select * from Round where lobbyId = :lobbyId and gameId=:gameId")
    fun getAllRounds(lobbyId: String, gameId: String): LiveData<List<LocalRound>>
}