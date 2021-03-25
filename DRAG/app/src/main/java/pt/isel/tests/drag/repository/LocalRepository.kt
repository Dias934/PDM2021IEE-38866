package pt.isel.tests.drag.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pt.isel.tests.drag.repository.converters.Converters
import pt.isel.tests.drag.repository.entities.*
import java.util.concurrent.Executors

class LocalRepository(database: DragDB) : ILocalRepository {

    private val lobbyDao = database.lobbyDao()
    private val playerDao = database.playerDao()
    private val gameDao = database.gameDao()

    private val executor = Executors.newSingleThreadExecutor()

    override fun createLobby(type: LobbyType, name: String, maxPlayers: Int, maxRounds: Int,
                             defaultPlayerName: String) = MutableLiveData<String>().apply {
                executor.submit{
                    val lobby = Lobby(type, name, maxPlayers, maxRounds, LobbyState.FULL)
                    val players = mutableListOf<LocalPlayer>()
                    for ( i in 0 until maxPlayers){
                        val player = LocalPlayer(lobby.id, defaultPlayerName.format(i), PlayerType.NORMAL,
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

    override fun renamePlayer(lobby: Lobby, oldPlayerName: String, newPlayerName: String) : LiveData<RepositoryResponse> {
        val resp= MutableLiveData<RepositoryResponse>()

        executor.submit {
            when{
                lobby.players.contains(newPlayerName) -> resp.postValue(RepositoryResponse.ALREADY_EXISTS)
                !lobby.players.contains(oldPlayerName) -> resp.postValue(RepositoryResponse.NOT_FOUND)
                else ->{
                    resp.postValue(RepositoryResponse.OK)
                    playerDao.updatePlayerName(oldPlayerName, newPlayerName)
                    lobby.players.remove(oldPlayerName)
                    lobby.players.add(newPlayerName)
                    lobbyDao.updatePlayers(lobby)
                }
            }
        }

        return resp
    }

    override fun createGame(lobby: Lobby): LiveData<String> {
        val gameDate = MutableLiveData<String>()

        executor.submit {
            val game = Game(lobby.id)
            gameDao.insert(game)
            lobbyDao.updateLobbyState(lobby.id, LobbyState.PLAYING)
            gameDate.postValue(game.id)
        }

        return gameDate
    }
}