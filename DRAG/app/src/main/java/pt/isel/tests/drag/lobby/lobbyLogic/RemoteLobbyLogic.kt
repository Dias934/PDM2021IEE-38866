package pt.isel.tests.drag.lobby.lobbyLogic

import androidx.lifecycle.LiveData
import pt.isel.tests.drag.observeOnce
import pt.isel.tests.drag.repository.RemoteRepository
import pt.isel.tests.drag.repository.entities.*

class RemoteLobbyLogic(override val repository: RemoteRepository, lobby: LiveData<Lobby>,
                       private val playerRef: String): AbstractLobbyLogic<RemotePlayer>(repository, lobby){

    lateinit var currentPlayer: RemotePlayer

    val livePlayer: LiveData<RemotePlayer> by lazy {
        val player = repository.getPlayer(playerRef)
        player.observeOnce{
            if(it != null)
                currentPlayer = it
        }
        player
    }

    private fun isCurrentPlayerInitialized() = this::currentPlayer.isInitialized

    fun isCurrentPlayerOwner() =
            isCurrentPlayerInitialized() && currentPlayer.type == PlayerType.OWNER


    fun isCurrentPlayerInLobby(players: List<RemotePlayer>) =
            isCurrentPlayerInitialized() && players.any { p -> p.docRef == currentPlayer.docRef }

    fun removePlayer(player: RemotePlayer) = repository.removePlayer(currentLobby, player)

    fun changePlayerState(state: PlayerState) = repository.changePlayerState(playerRef, state)

}