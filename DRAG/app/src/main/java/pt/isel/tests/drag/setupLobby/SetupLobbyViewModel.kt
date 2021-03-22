package pt.isel.tests.drag.setupLobby

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import pt.isel.tests.drag.localRepository
import pt.isel.tests.drag.remoteRepository
import pt.isel.tests.drag.repository.IRemoteRepository
import pt.isel.tests.drag.repository.LobbyType
import pt.isel.tests.drag.repository.Player


class SetupLobbyViewModel(private val app: Application, private val state: SavedStateHandle):
    AndroidViewModel(app) {


    val lobbyType: LobbyType by lazy { state.get<LobbyType>(LOBBY_TYPE)?: LobbyType.LOCAL }

    private val repository by lazy {
            if(lobbyType == LobbyType.LOCAL)
                    app.localRepository
            else
                    app.remoteRepository
    }

    lateinit var lobbyId: LiveData<String>
    lateinit var player: LiveData<Player>

    fun createLobby(name: String, players: Int, rounds: Int, defaultPlayerName: String) {
            lobbyId = repository.createLobby(lobbyType, name, players, rounds, defaultPlayerName)
    }

    fun getPlayerOwner(createdLobby: String){
        player = (repository as IRemoteRepository).getOwnerFromOpeningLobby(createdLobby)
    }


}