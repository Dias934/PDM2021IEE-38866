package pt.isel.tests.drag.repository

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.util.*

const val MIN_N_PLAYERS = 5
const val MIN_N_ROUNDS = 1
const val START_ROUND_NUMBER = 1
const val START_TURN_NUMBER = 0


@Entity(tableName = "Lobby")
data class Lobby(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name ="name")
    var name: String = "",

    @ColumnInfo(name ="maxPlayers" )
    var nPlayers : Int = MIN_N_PLAYERS,

    @ColumnInfo(name = "maxRounds")
    var nRound : Int = MIN_N_ROUNDS,

    @ColumnInfo(name = "state")
    var state : LobbyState = LobbyState.OPEN,

    @ColumnInfo(name = "players")
    var players: MutableList<String> = mutableListOf()
)

enum class LobbyState {
    OPEN, PLAYING, CLOSED
}

@Entity(
    tableName = "Player",
    primaryKeys = ["lobbyId", "id"],
    foreignKeys = [
        ForeignKey(
            onDelete = CASCADE,
            childColumns = ["lobbyId"],
            parentColumns = ["id"],
            entity = Lobby::class
        )
    ]
)
data class Player(
    @ColumnInfo( name = "lobbyId")
    var lobbyId: String,

    @ColumnInfo(name ="id")
    var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "name")
    var name: String ="",

    @ColumnInfo(name = "type")
    var type: PlayerType = PlayerType.NORMAL
)

enum class PlayerType {
    MASTER, NORMAL
}

data class Round(
    var lobbyId: String,

    var order: MutableList<String>,

    var number: Int = START_ROUND_NUMBER,

)

data class Turn(
    var lobbyId: String,

    var roundNumber: Int,

    var type: TurnType,

    var play: String,

    var nTurn: Int = START_ROUND_NUMBER
)

enum class TurnType{
    WORD, DRAW
}


