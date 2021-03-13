package pt.isel.tests.drag.setupLobby

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import pt.isel.tests.drag.repository.LobbyType



class SetupLobbyViewModel(private val app: Application, private val state: SavedStateHandle):
    AndroidViewModel(app) {

        val lobbyType: LobbyType by lazy { state.get<LobbyType>(TYPE_FIELD)?: LobbyType.LOCAL }
}