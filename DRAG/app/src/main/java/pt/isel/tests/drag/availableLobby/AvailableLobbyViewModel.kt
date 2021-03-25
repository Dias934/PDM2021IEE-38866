package pt.isel.tests.drag.availableLobby

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import pt.isel.tests.drag.R
import pt.isel.tests.drag.remoteRepository
import pt.isel.tests.drag.repository.entities.Lobby
import pt.isel.tests.drag.repository.entities.LobbyState
import pt.isel.tests.drag.repository.entities.PlayerType

class AvailableLobbyViewModel (private val app: Application, private val state: SavedStateHandle):
    AndroidViewModel(app) {


    private val repository by lazy{ app.remoteRepository}

    val lobbys: LiveData<List<Lobby>> by lazy {
        repository.getLobbys(LobbyState.OPEN)
    }

    lateinit var playerRef: LiveData<String>

    fun createPlayer(lobby: Lobby) {
        playerRef = repository.createPlayer(lobby, app.resources.getString(R.string.player_value).format(lobby.players.size), PlayerType.NORMAL)
    }

}