package pt.isel.tests.drag.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import pt.isel.tests.drag.repository.entities.*
import kotlin.random.Random
import kotlin.random.nextInt

private const val LOBBY_COLLECTION ="LOBBY"
private const val PLAYER_COLLECTION = "PLAYER"
private const val GAME_COLLECTION = "GAME"

private const val LOBBY_PLAYERS_FIELD = "players"
private const val PLAYER_LOBBY_ID_FIELD = "lobbyId"
private const val NAME_FIELD = "name"
private const val STATE_FIELD = "state"
private const val TYPE_FIELD = "type"

private const val CONNECTED_REF =".info/connected"
private const val PLAYERS_REF = "Players"

class RemoteRepository(private val dbStorage: FirebaseFirestore) : IRemoteRepository {

    private val lobbyRef = dbStorage.collection(LOBBY_COLLECTION)
    private val playerRef = dbStorage.collection(PLAYER_COLLECTION)
    private val gameRef = dbStorage.collection(GAME_COLLECTION)

    override fun createLobby(type: LobbyType, name: String, maxPlayers: Int, maxRounds: Int,
                             defaultPlayerName: String): LiveData<String> {
        val lobbyLD = MutableLiveData<String>()
        val lobby = Lobby(type, name, maxPlayers, maxRounds, LobbyState.OPENING)
        val player = RemotePlayer(lobby.id, defaultPlayerName.format(0), PlayerType.OWNER, PlayerState.READY)

        dbStorage.runTransaction{ transaction ->
            transaction.set( lobbyRef.document(lobby.id), lobby)
                    .set( playerRef.document(player.docRef), player)
            lobby.players.add(player.docRef)
            transaction.update( lobbyRef.document(lobby.id), LOBBY_PLAYERS_FIELD, lobby.players)
                    .set(playerRef.document(player.docRef), player)
        }.addOnSuccessListener {
            lobbyLD.postValue(lobby.id)
        }

        return lobbyLD
    }

    override fun getLobby(lobbyId: String): LiveData<Lobby> {
        val lobbyLD = MutableLiveData<Lobby>()

        lobbyRef.document(lobbyId)
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
        lobbyRef.whereEqualTo(STATE_FIELD, state)
                .addSnapshotListener { data, error ->
                    if(error == null && data != null){
                        val lobbys = data.toObjects(Lobby::class.java)
                        lobbyLD.postValue(lobbys)
                    }
                }
        return lobbyLD
    }

    override fun getOwnerFromOpeningLobby(lobbyId: String): LiveData<RemotePlayer> {
        val playerLD = MutableLiveData<RemotePlayer>()

        playerRef.whereEqualTo(PLAYER_LOBBY_ID_FIELD, lobbyId)
                .whereEqualTo(TYPE_FIELD, PlayerType.OWNER)
                .get()
                .addOnSuccessListener {snap ->
                    dbStorage.collection(LOBBY_COLLECTION)
                            .document(lobbyId)
                            .update(STATE_FIELD, LobbyState.OPEN)
                            .addOnSuccessListener {
                                val player = snap.toObjects<RemotePlayer>().first()
                                playerLD.postValue(player)
                            }
                }
        return playerLD
    }


    override fun createPlayer(lobby: Lobby, playerName: String, playerType: PlayerType): MutableLiveData<String> {
        val playerLD = MutableLiveData<String>()
        var name = playerName.format(Random.nextInt(0 .. 500))
        var player = RemotePlayer(lobby.id, name, playerType)
        while(lobby.players.contains(player.name)){
            name = playerName.format(Random.nextInt(0 .. 500))
            player = RemotePlayer(lobby.id, name, playerType)
        }

        dbStorage.runTransaction { transaction ->
            lobby.players.add(player.docRef)
            if(lobby.players.size == lobby.nPlayers)
                lobby.state = LobbyState.FULL
            transaction
                    .set(lobbyRef.document(lobby.id), lobby, SetOptions.merge())
                    .set(playerRef.document(player.docRef), player)
        }.addOnSuccessListener {
            playerLD.postValue(player.docRef)
        }

        return playerLD
    }

    override fun getPlayer(playerRef: String): LiveData<RemotePlayer> {
        val playerLD = MutableLiveData<RemotePlayer>()

        this.playerRef
                .document(playerRef)
                .addSnapshotListener { data, error ->
                    if(error == null && data != null){
                        val player = data.toObject<RemotePlayer>()
                        playerLD.postValue(player)
                    }
                }

        return playerLD
    }

    override fun getPlayers(lobbyId: String): LiveData<List<RemotePlayer>> {
        val playersLD = MutableLiveData<List<RemotePlayer>>()

        playerRef
            .whereEqualTo(PLAYER_LOBBY_ID_FIELD, lobbyId)
            .addSnapshotListener { data, error ->
                if(error == null && data != null){
                    val players = data.toObjects<RemotePlayer>()
                    playersLD.postValue(players)
                }
            }
        return playersLD
    }

    override fun renamePlayer(lobby: Lobby, oldPlayerName: String, newPlayerName: String):
            LiveData<RepositoryResponse> {
        val resp = MutableLiveData<RepositoryResponse>()

        playerRef
                .whereEqualTo(PLAYER_LOBBY_ID_FIELD, lobby.id)
                .get()
                .addOnSuccessListener { query ->
                    val players = query.documents.mapNotNull { docs -> docs.toObject<RemotePlayer>() }
                    when {
                        players.any { p -> p.name == newPlayerName } ->
                            resp.postValue(RepositoryResponse.ALREADY_EXISTS)
                        players.any { p -> p.name == oldPlayerName } -> {
                            val player = players.first { p -> p.name == oldPlayerName }
                            playerRef.document(player.docRef)
                                    .update(NAME_FIELD, newPlayerName)
                                    .addOnSuccessListener {
                                        resp.postValue(RepositoryResponse.OK)
                                    }
                                    .addOnFailureListener {
                                        resp.postValue(RepositoryResponse.FAILED)
                                    }
                        }
                        else -> resp.postValue(RepositoryResponse.NOT_FOUND)
                    }
                }
                .addOnFailureListener {
                    resp.postValue(RepositoryResponse.FAILED)
                }
        return resp
    }

    override fun changePlayerState(playerRef: String, playerState: PlayerState):
            LiveData<RepositoryResponse>{
        val resp = MutableLiveData<RepositoryResponse>()
        this.playerRef
                .document(playerRef)
                .update(STATE_FIELD, playerState)
                .addOnSuccessListener {
                    resp.postValue(RepositoryResponse.OK)
                }
                .addOnFailureListener {
                    resp.postValue(RepositoryResponse.NOT_FOUND)
                }
        return resp
    }

    override fun removePlayer(lobby: Lobby, player: RemotePlayer):
            LiveData<RepositoryResponse> {
        val resp = MutableLiveData<RepositoryResponse>()

        dbStorage.runTransaction { transaction ->
            lobby.players.remove(player.docRef)
            if(lobby.players.size < lobby.nPlayers)
                lobby.state = LobbyState.OPEN

            transaction.set(lobbyRef.document(lobby.id), lobby, SetOptions.merge())
                    .delete(playerRef.document(player.docRef))

        }.addOnSuccessListener {
            resp.postValue(RepositoryResponse.OK)
        }.addOnFailureListener {
            resp.postValue(RepositoryResponse.FAILED)
        }
        return resp
    }

    override fun createGame(lobby: Lobby): LiveData<String> {
        val gameLD = MutableLiveData<String>()
        val game = Game(lobby.id)

        dbStorage.runTransaction { transaction ->
            transaction.update(lobbyRef.document(lobby.id), STATE_FIELD, LobbyState.PLAYING)
                    .set(gameRef.document(game.id), game)
        }.addOnSuccessListener {
            gameLD.postValue(game.id)
        }

        return gameLD
    }
}