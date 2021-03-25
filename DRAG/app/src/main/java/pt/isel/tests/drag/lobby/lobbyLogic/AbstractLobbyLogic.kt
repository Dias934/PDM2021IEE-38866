package pt.isel.tests.drag.lobby.lobbyLogic

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import pt.isel.tests.drag.repository.IRepository
import pt.isel.tests.drag.repository.LocalRepository
import pt.isel.tests.drag.repository.RepositoryResponse
import pt.isel.tests.drag.repository.entities.*

abstract class AbstractLobbyLogic<Player>(
        protected open val repository: IRepository<Player, *>,
        val lobby: LiveData<Lobby>) where Player: IPlayer {

    lateinit var currentLobby: Lobby

    val players: LiveData<List<Player>> by lazy{
        Transformations.switchMap(lobby){
            currentLobby = it
            repository.getPlayers(it.id)
        }
    }

    fun renamePlayer(oldName: String, newName: String): LiveData<RepositoryResponse> =
            repository.renamePlayer(currentLobby, oldName, newName)

    fun createGame() = repository.createGame(currentLobby)
}


