package pt.isel.tests.drag.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt

private const val LOBBY_COLLECTION ="LOBBY"
private const val PLAYER_COLLECTION = "PLAYER"

private const val LOBBY_PLAYERS_FIELD = "players"
private const val PLAYER_LOBBY_ID_FIELD = "lobbyId"
private const val NAME_FIELD = "name"
private const val STATE_FIELD = "state"
private const val TYPE_FIELD = "type"

private const val CONNECTED_REF =".info/connected"
private const val PLAYERS_REF = "Players"

class RemoteRepository(private val dbStorage: FirebaseFirestore) : IRemoteRepository {

    override fun createLobby(type: LobbyType, name: String, maxPlayers: Int, maxRounds: Int,
                             defaultPlayerName: String): LiveData<String> {
        val lobbyLD = MutableLiveData<String>()
        val lobby = Lobby(type, name, maxPlayers, maxRounds, LobbyState.OPENING)
        val player = Player(lobby.id, defaultPlayerName.format(0), PlayerType.OWNER, PlayerState.READY)

        dbStorage.runTransaction{ transaction ->
            transaction
                    .set(dbStorage.collection(LOBBY_COLLECTION).document(lobby.id), lobby)
                    .set(dbStorage.collection(PLAYER_COLLECTION).document(lobby.id + player.name), player)
            lobby.players.add(player.name)
            transaction
                    .update(dbStorage.collection(LOBBY_COLLECTION).document(lobby.id),
                            LOBBY_PLAYERS_FIELD, lobby.players)
        }.addOnSuccessListener {
            lobbyLD.postValue(lobby.id)
        }

        return lobbyLD
    }

    override fun getLobby(lobbyId: String): LiveData<Lobby> {
        val lobbyLD = MutableLiveData<Lobby>()

        dbStorage.collection(LOBBY_COLLECTION)
                .document(lobbyId)
                .addSnapshotListener { data, error ->
                    if(error == null && data != null){
                        val lobby = data.toObject<Lobby>()
                        lobbyLD.postValue(lobby)
                    }
                }
        return lobbyLD
    }

    override fun getLobbys(state: LobbyState): LiveData<List<Lobby>> {
        val lobbyLD = MutableLiveData<List<Lobby>>()
        dbStorage
                .collection(LOBBY_COLLECTION)
                .whereEqualTo(STATE_FIELD, state)
                .addSnapshotListener { data, error ->
                    if(error == null && data != null){
                        val lobbys = data.toObjects(Lobby::class.java)
                        lobbyLD.postValue(lobbys)
                    }
                }
        return lobbyLD
    }

    override fun getOwnerFromOpeningLobby(lobbyId: String): LiveData<Player> {
        val playerLD = MutableLiveData<Player>()

        dbStorage
                .collection(PLAYER_COLLECTION)
                .whereEqualTo(PLAYER_LOBBY_ID_FIELD, lobbyId)
                .whereEqualTo(TYPE_FIELD, PlayerType.OWNER)
                .get()
                .addOnSuccessListener {snap ->
                    dbStorage.collection(LOBBY_COLLECTION)
                            .document(lobbyId)
                            .update(STATE_FIELD, LobbyState.OPEN)
                            .addOnSuccessListener {
                                val player = snap.toObjects<Player>().first()
                                playerLD.postValue(player)
                            }
                }
        return playerLD
    }


    override fun createPlayer(lobby: Lobby, playerName: String, playerType: PlayerType): MutableLiveData<String> {
        val playerLD = MutableLiveData<String>()
        var name = playerName.format(Random.nextInt(0 .. 500))
        var player = Player(lobby.id, name, playerType)

        dbStorage.runTransaction { transaction ->
            while(lobby.players.contains(player.name)){
                name = playerName.format(Random.nextInt(0 .. 500))
                player = Player(lobby.id, name, playerType)
            }
            lobby.players.add(player.name)
            if(lobby.players.size == lobby.nPlayers)
                lobby.state = LobbyState.FULL
            transaction
                    .set(dbStorage.collection(LOBBY_COLLECTION).document(lobby.id), lobby, SetOptions.merge())
                    .set(dbStorage.collection(PLAYER_COLLECTION).document(player.name), player)
        }.addOnSuccessListener {
            playerLD.postValue(player.name)
        }

        return playerLD
    }

    override fun getPlayer(lobbyId: String, playerName: String): LiveData<Player> {
        val playerLD = MutableLiveData<Player>()
        dbStorage
            .collection(PLAYER_COLLECTION)
            .document(lobbyId + playerName)
            .addSnapshotListener { data, error ->
                if(error == null && data != null){
                    val player = data.toObject<Player>()
                    playerLD.postValue(player)
                }
            }
        return playerLD
    }

    override fun getPlayers(lobbyId: String): LiveData<List<Player>> {
        val playersLD = MutableLiveData<List<Player>>()

        dbStorage.collection(PLAYER_COLLECTION)
            .whereEqualTo(PLAYER_LOBBY_ID_FIELD, lobbyId)
            .addSnapshotListener { data, error ->
                if(error == null && data != null){
                    val players = data.toObjects<Player>()
                    playersLD.postValue(players)
                }
            }
        return playersLD
    }

    override fun renamePlayer(lobby: Lobby, playerId: String, playerName: String): LiveData<Boolean> {
        dbStorage.runTransaction { transaction ->

        }
        return MutableLiveData()
    }

    override fun changePlayerState(lobbyId: String, playerName: String, playerState: PlayerState) {
        dbStorage.collection(PLAYER_COLLECTION)
                .document(playerName)
                .update(STATE_FIELD, playerState)
    }

    override fun removePlayer(lobby: Lobby, player: Player) {
        dbStorage.runTransaction { transaction ->
            lobby.players.remove(player.name)
            if(lobby.players.size < lobby.nPlayers)
                lobby.state = LobbyState.OPEN
            transaction
                    .set(dbStorage.collection(LOBBY_COLLECTION).document(lobby.id),
                            lobby, SetOptions.merge())
                    .delete(dbStorage.collection(PLAYER_COLLECTION).document(player.name))

        }

    }
}