package pt.isel.tests.drag.availableLobby

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import pt.isel.tests.drag.R
import pt.isel.tests.drag.remoteRepository
import pt.isel.tests.drag.repository.Lobby
import pt.isel.tests.drag.repository.PlayerType

class AvailableLobbyViewModel (private val app: Application, private val state: SavedStateHandle):
    AndroidViewModel(app) {


    private val repository by lazy{ app.remoteRepository}

    val lobbys: LiveData<List<Lobby>> by lazy {
        repository.getLobbys()
    }

    lateinit var playerId: LiveData<String>

    fun createPlayer(lobbyId: String) {
        playerId = repository.createPlayer(lobbyId, app.resources.getString(R.string.player_name_string), PlayerType.NORMAL)
    }

}