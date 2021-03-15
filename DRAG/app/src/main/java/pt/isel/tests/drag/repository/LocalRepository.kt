package pt.isel.tests.drag.repository

import androidx.lifecycle.MutableLiveData
import java.util.concurrent.Executors

class LocalRepository(private val database: Database) {

    private val lobbyDao = database.lobbyDao()
    private val playerDao = database.playerDao()

    private val executor = Executors.newSingleThreadExecutor()

    fun createLobby(type: LobbyType, name: String, maxPlayers: Int, maxRounds: Int) : MutableLiveData<String> =
            MutableLiveData<String>().apply {
                executor.submit{
                    val lobby = Lobby(type, name, maxPlayers, maxRounds, LobbyState.FULL)
                    lobbyDao.insert(lobby)
                    this.postValue(lobby.id)
                    for ( i in 0..maxPlayers){
                        val player = Player("$i", PlayerType.NORMAL, lobby.id)
                        playerDao.insert(player)
                        lobby.players.add(player.id)
                    }
                    lobbyDao.updatePlayers(lobby)
                }
            }

}