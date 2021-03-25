package pt.isel.tests.drag.lobby

import android.app.Application
import androidx.lifecycle.*
import pt.isel.tests.drag.lobby.lobbyLogic.LocalLobbyLogic
import pt.isel.tests.drag.lobby.lobbyLogic.RemoteLobbyLogic
import pt.isel.tests.drag.localRepository
import pt.isel.tests.drag.remoteRepository
import pt.isel.tests.drag.repository.*
import pt.isel.tests.drag.repository.entities.*
import pt.isel.tests.drag.setupLobby.LOBBY_TYPE
import java.lang.IllegalStateException

class LobbyViewModel(private val app: Application, private val state: SavedStateHandle):AndroidViewModel(app) {

    val lobbyType by lazy {
        state.get<LobbyType>(LOBBY_TYPE) ?: throw IllegalStateException("Type of lobby not found!")
    }

    val lobbyId by lazy{
        state.get<String>(LOBBY_ID) ?: throw IllegalStateException("Lobby not found!")
    }

    private val repository by lazy {
        if(lobbyType == LobbyType.LOCAL)
            app.localRepository
        else
            app.remoteRepository
    }

    val lobby: LiveData<Lobby> by lazy {
        repository.getLobby(lobbyId)
    }

    val lobbyLogic by lazy {
        if(lobbyType == LobbyType.LOCAL)
            LocalLobbyLogic(repository as LocalRepository, lobby)
        else{
            val playerRef = state.get<String>(PLAYER_REF) ?: throw IllegalStateException("Player id not found!")
            RemoteLobbyLogic(repository as RemoteRepository, lobby, playerRef)
        }
    }
}