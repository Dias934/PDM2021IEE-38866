package pt.isel.tests.drag.repository

import androidx.lifecycle.LiveData
import pt.isel.tests.drag.repository.entities.*

interface IRepository <Player, Round> where Player: IPlayer, Round: IRound {
    fun createLobby(type: LobbyType, name: String, maxPlayers: Int, maxRounds: Int, defaultPlayerName: String) : LiveData<String>
    fun getLobby(lobbyId: String) : LiveData<Lobby>
    fun getPlayers(lobbyId: String) : LiveData<List<Player>>
    fun renamePlayer(lobby: Lobby, oldPlayerName: String, newPlayerName: String): LiveData<RepositoryResponse>
    fun createGame(lobby: Lobby): LiveData<String>
}

interface ILocalRepository : IRepository<LocalPlayer, LocalRound>

interface IRemoteRepository : IRepository<RemotePlayer, RemoteRound>{
    fun getLobbys(state: LobbyState = LobbyState.OPEN):LiveData<List<Lobby>>
    fun getOwnerFromOpeningLobby(lobbyId: String): LiveData<RemotePlayer>
    fun createPlayer(lobby: Lobby, playerName: String, playerType: PlayerType) :LiveData<String>
    fun getPlayer(playerRef: String) : LiveData<RemotePlayer>
    fun changePlayerState(playerRef: String, playerState: PlayerState): LiveData<RepositoryResponse>
    fun removePlayer(lobby: Lobby, player: RemotePlayer): LiveData<RepositoryResponse>
}

enum class RepositoryResponse{
    OK, ALREADY_EXISTS, NOT_FOUND, FAILED
}