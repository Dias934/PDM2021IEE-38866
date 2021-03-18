package pt.isel.tests.drag.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface IRepository {
    fun createLobby(type: LobbyType, name: String, maxPlayers: Int, maxRounds: Int, defaultPlayerName: String) : LiveData<String>
    fun getLobby(lobbyId: String) : LiveData<Lobby>
    fun getPlayers(lobbyId: String) : LiveData<List<Player>>
    fun updatePlayer(player: Player)
}

interface ILocalRepository : IRepository{

}

interface IRemoteRepository : IRepository{
    fun createPlayer(lobby: Lobby, name: String, type: PlayerType) :LiveData<String>
    fun getPlayer(playerId: String) : LiveData<Player>
    fun getLobbys(state: LobbyState = LobbyState.OPEN):LiveData<List<Lobby>>
    fun getCreatedPlayer(createdLobby: String): LiveData<Player>
    fun removePlayer(lobby: Lobby, player: Player)
}