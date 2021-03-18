package pt.isel.tests.drag.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects

private const val LOBBY_COLLECTION ="LOBBY"
private const val PLAYER_COLLECTION = "PLAYER"

private const val LOBBY_PLAYERS_FIELD = "players"
private const val PLAYER_LOBBY_ID_FIELD = "lobbyId"
private const val STATE_FIELD = "state"
private const val TYPE_FIELD = "type"

class RemoteRepository(private val database: FirebaseFirestore) : IRemoteRepository {


    override fun createPlayer(lobby: Lobby, name: String, type: PlayerType): MutableLiveData<String> {
        val playerLD = MutableLiveData<String>()
        val player = Player(name, type, lobbyId = lobby.id)

        database.runTransaction { transaction ->
            lobby.players.add(player.id)
            if(lobby.players.size == lobby.nPlayers)
                lobby.state = LobbyState.FULL
            transaction
                    .set(database.collection(LOBBY_COLLECTION).document(lobby.id), lobby, SetOptions.merge())
                    .set(database.collection(PLAYER_COLLECTION).document(player.id), player)
        }.addOnSuccessListener {
            playerLD.postValue(player.id)
        }

        return playerLD
    }

    override fun getPlayer(playerId: String): LiveData<Player> {
        val playerLD = MutableLiveData<Player>()
        database
            .collection(PLAYER_COLLECTION)
            .document(playerId)
            .addSnapshotListener { data, error ->
                if(error == null && data != null){
                    val player = data.toObject<Player>()
                    playerLD.postValue(player)
                }
            }
        return playerLD
    }

    override fun getCreatedPlayer(createdLobby: String): LiveData<Player> {
        val playerLD = MutableLiveData<Player>()
        database
            .collection(PLAYER_COLLECTION)
            .whereEqualTo(PLAYER_LOBBY_ID_FIELD, createdLobby)
            .whereEqualTo(TYPE_FIELD, PlayerType.OWNER)
            .get()
            .addOnSuccessListener {snap ->
                val player = snap.toObjects<Player>().first()
                playerLD.postValue(player)
            }
        return playerLD
    }

    override fun getLobbys(state: LobbyState): LiveData<List<Lobby>> {
        val lobbyLD = MutableLiveData<List<Lobby>>()
        database
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

    override fun createLobby(type: LobbyType, name: String, maxPlayers: Int, maxRounds: Int,
                             defaultPlayerName: String): LiveData<String> {
        val lobbyLD = MutableLiveData<String>()

        val lobby = Lobby(type, name, maxPlayers, maxRounds, LobbyState.OPENING)

        val player = Player(defaultPlayerName.format(0), PlayerType.OWNER, PlayerState.READY, lobby.id)

        database.runTransaction{transaction ->
            transaction
                .set(database.collection(LOBBY_COLLECTION).document(lobby.id), lobby)
                .set(database.collection(PLAYER_COLLECTION).document(player.id), player)
            lobby.players.add(player.id)
            lobby.state = LobbyState.OPEN
            transaction
                .update(database.collection(LOBBY_COLLECTION).document(lobby.id)
                    ,LOBBY_PLAYERS_FIELD, lobby.players, STATE_FIELD, lobby.state)
        }. addOnSuccessListener {
                lobbyLD.postValue(lobby.id)
        }

        return lobbyLD
    }

    override fun getLobby(lobbyId: String): LiveData<Lobby> {
        val lobbyLD = MutableLiveData<Lobby>()

        database.collection(LOBBY_COLLECTION)
            .document(lobbyId)
            .addSnapshotListener { data, error ->
                if(error == null && data != null){
                    val lobby = data.toObject<Lobby>()
                    lobbyLD.postValue(lobby)
                }
            }
        return lobbyLD
    }

    override fun getPlayers(lobbyId: String): LiveData<List<Player>> {
        val playersLD = MutableLiveData<List<Player>>()

        database.collection(PLAYER_COLLECTION)
            .whereEqualTo(PLAYER_LOBBY_ID_FIELD, lobbyId)
            .addSnapshotListener { data, error ->
                if(error == null && data != null){
                    val players = data.toObjects<Player>()
                    playersLD.postValue(players)
                }
            }
        return playersLD
    }

    override fun updatePlayer(player: Player) {
        database.collection(PLAYER_COLLECTION)
            .document(player.id)
            .set(player, SetOptions.merge())
    }

    override fun removePlayer(lobby: Lobby, player: Player) {
        database.runTransaction { transaction ->
            lobby.players.remove(player.id)
            if(lobby.players.size < lobby.nPlayers)
                lobby.state = LobbyState.OPEN
            transaction
                    .set(database.collection(LOBBY_COLLECTION).document(lobby.id),
                            lobby, SetOptions.merge())
                    .delete(database.collection(PLAYER_COLLECTION).document(player.id))

        }
    }
}