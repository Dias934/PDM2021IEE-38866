package pt.isel.tests.drag.lobby

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import pt.isel.tests.drag.localRepository
import pt.isel.tests.drag.repository.Lobby
import pt.isel.tests.drag.repository.LobbyType
import pt.isel.tests.drag.repository.Player
import pt.isel.tests.drag.setupLobby.TYPE_FIELD
import java.lang.IllegalStateException

class LobbyViewModel(private val app: Application, private val state: SavedStateHandle):AndroidViewModel(app) {

    private val repository by lazy {
        val lobbyType: LobbyType = state.get<LobbyType>(TYPE_FIELD)
            ?: throw IllegalStateException("Type of lobby not found!")
        if(lobbyType == LobbyType.LOCAL)
            app.localRepository
        else
            app.localRepository
    }

    val lobby: LiveData<Lobby> by lazy {
        val lobbyId: String = state.get<String>(LOBBY_ID)
            ?: throw IllegalStateException("Lobby not found!")
        repository.getLobby(lobbyId)
    }

    val players: LiveData<MutableList<Player>> by lazy {
        Transformations.switchMap(lobby){
            repository.getPlayers(it.id)
        }
    }

    fun updatePlayerName(player: Player) = repository.updatePlayer(player)

}