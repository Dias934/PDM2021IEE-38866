package pt.isel.tests.drag.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import java.util.concurrent.Executors

class LocalRepository(private val database: DragDB) : IRepository {

    private val lobbyDao = database.lobbyDao()
    private val playerDao = database.playerDao()

    private val executor = Executors.newSingleThreadExecutor()

    override fun createLobby(type: LobbyType, name: String, maxPlayers: Int, maxRounds: Int,
                             defaultPlayerName: String) = MutableLiveData<String>().apply {
                executor.submit{
                    val lobby = Lobby(type, name, maxPlayers, maxRounds, LobbyState.FULL)
                    val players = mutableListOf<Player>()
                    for ( i in 0 until maxPlayers){
                        val player = Player(lobby.id, defaultPlayerName.format(i), PlayerType.NORMAL,
                            PlayerState.READY)
                        lobby.players.add(player.name)
                        players.add(player)
                    }
                    lobbyDao.insert(lobby)
                    this.postValue(lobby.id)
                    playerDao.insertAll(players)
                }
            }

    override fun getLobby(lobbyId: String) = lobbyDao.get(lobbyId)

    override fun getPlayers(lobbyId: String) = playerDao.getAllPlayers(lobbyId)

    override fun renamePlayer(lobby: Lobby, oldPlayerName: String, newPlayerName: String) : LiveData<Boolean> {
        val resp= MutableLiveData<Boolean>()

        executor.submit {
            if (lobby.players.contains(newPlayerName) || !lobby.players.contains(oldPlayerName))
                resp.postValue(false)
            else {
                resp.postValue(true)
                playerDao.updatePlayerName(oldPlayerName, newPlayerName)
                lobby.players.remove(oldPlayerName)
                lobby.players.add(newPlayerName)
                lobbyDao.updatePlayers(lobby)
            }
        }

        return resp
    }

}