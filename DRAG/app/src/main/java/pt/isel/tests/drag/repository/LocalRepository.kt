package pt.isel.tests.drag.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.Executors

class LocalRepository(private val database: Database) {

    private val lobbyDao = database.lobbyDao()
    private val playerDao = database.playerDao()

    private val executor = Executors.newSingleThreadExecutor()

    fun createLobby(type: LobbyType, name: String, maxPlayers: Int, maxRounds: Int,
                    defaultPlayerName: String) : MutableLiveData<String> =
            MutableLiveData<String>().apply {
                executor.submit{
                    val lobby = Lobby(type, name, maxPlayers, maxRounds, LobbyState.FULL)
                    val players = mutableListOf<Player>()
                    for ( i in 0 until maxPlayers){
                        val player = Player(defaultPlayerName.format(i), PlayerType.NORMAL,
                            PlayerState.READY, lobby.id)
                        lobby.players.add(player.id)
                        players.add(player)
                    }
                    lobbyDao.insert(lobby)
                    this.postValue(lobby.id)
                    playerDao.insertAll(players)
                }
            }

    fun getLobby(lobbyId: String) : LiveData<Lobby> = lobbyDao.get(lobbyId)

    fun getPlayers(lobbyId: String) : LiveData<MutableList<Player>> = playerDao.getAllPlayers(lobbyId)
    fun updatePlayer(player: Player) {
        executor.submit {
            playerDao.update(player)
        }
    }


}