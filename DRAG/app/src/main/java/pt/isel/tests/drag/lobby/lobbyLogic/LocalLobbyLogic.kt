package pt.isel.tests.drag.lobby.lobbyLogic

import androidx.lifecycle.LiveData
import pt.isel.tests.drag.repository.LocalRepository
import pt.isel.tests.drag.repository.entities.Lobby
import pt.isel.tests.drag.repository.entities.LocalPlayer

class LocalLobbyLogic(repository: LocalRepository, lobby: LiveData<Lobby>) :
        AbstractLobbyLogic<LocalPlayer>(repository, lobby)