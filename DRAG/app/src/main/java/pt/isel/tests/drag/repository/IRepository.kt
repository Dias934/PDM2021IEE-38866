package pt.isel.tests.drag.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface IRepository {
    fun createLobby(type: LobbyType, name: String, maxPlayers: Int, maxRounds: Int, defaultPlayerName: String) : LiveData<String>
    fun getLobby(lobbyId: String) : LiveData<Lobby>
    fun getPlayers(lobbyId: String) : LiveData<List<Player>>
    fun renamePlayer(lobby: Lobby, oldPlayerName: String, newPlayerName: String): LiveData<Boolean>

}

interface IRemoteRepository : IRepository{
    fun getLobbys(state: LobbyState = LobbyState.OPEN):LiveData<List<Lobby>>
    fun getOwnerFromOpeningLobby(lobbyId: String): LiveData<Player>
    fun createPlayer(lobby: Lobby, playerName: String, playerType: PlayerType) :LiveData<String>
    fun getPlayer(lobbyId: String, playerName: String) : LiveData<Player>
    fun changePlayerState(lobbyId: String, playerName: String, playerState: PlayerState)
    fun removePlayer(lobby: Lobby, player: Player)
}