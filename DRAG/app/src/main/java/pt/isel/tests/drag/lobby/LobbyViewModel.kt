package pt.isel.tests.drag.lobby

import android.app.Application
import androidx.lifecycle.*
import pt.isel.tests.drag.localRepository
import pt.isel.tests.drag.remoteRepository
import pt.isel.tests.drag.repository.IRemoteRepository
import pt.isel.tests.drag.repository.Lobby
import pt.isel.tests.drag.repository.LobbyType
import pt.isel.tests.drag.repository.Player
import pt.isel.tests.drag.setupLobby.LOBBY_TYPE
import java.lang.IllegalStateException

class LobbyViewModel(private val app: Application, private val state: SavedStateHandle):AndroidViewModel(app) {

    val lobbyType by lazy { state.get<LobbyType>(LOBBY_TYPE) ?: throw IllegalStateException("Type of lobby not found!") }

    private val repository by lazy {
        if(lobbyType == LobbyType.LOCAL)
            app.localRepository
        else
            app.remoteRepository
    }

    private val lobbyId by lazy{ state.get<String>(LOBBY_ID) ?: throw IllegalStateException("Lobby not found!")}

    val lobby: LiveData<Lobby> by lazy {
        repository.getLobby(lobbyId)
    }

    val player by lazy {
        if(lobbyType == LobbyType.REMOTE){
            val id = state.get<String>(PLAYER_ID) ?: throw IllegalStateException("Player id not found!")
            (repository as IRemoteRepository).getPlayer(id)
        }
        else
            MutableLiveData(Player())
    }

    lateinit var currentLobby: Lobby
    lateinit var currentPlayer: Player

    fun isCurrentPlayerInitialized() = this::currentPlayer.isInitialized

    val players: LiveData<List<Player>> by lazy {
        Transformations.switchMap(lobby){
            currentLobby = it
            repository.getPlayers(it.id)
        }
    }

    fun updatePlayer(player: Player) = repository.updatePlayer(player)

    fun removePlayer(player: Player) = (repository as IRemoteRepository).removePlayer(currentLobby, player)

}