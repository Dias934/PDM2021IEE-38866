package pt.isel.tests.drag.setupLobby

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import pt.isel.tests.drag.localRepository
import pt.isel.tests.drag.repository.LobbyType



class SetupLobbyViewModel(private val app: Application, private val state: SavedStateHandle):
    AndroidViewModel(app) {


        val lobbyType: LobbyType by lazy { state.get<LobbyType>(TYPE_FIELD)?: LobbyType.LOCAL }

        private val repository by lazy {
                if(lobbyType == LobbyType.LOCAL)
                        app.localRepository
                else
                        app.localRepository
        }

        lateinit var lobbyId: LiveData<String>

        fun createLobby(name: String, players: Int, rounds: Int, defaultPlayerName: String) {
                lobbyId = repository.createLobby(lobbyType, name, players, rounds, defaultPlayerName)
        }
}