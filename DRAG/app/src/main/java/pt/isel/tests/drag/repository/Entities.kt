package pt.isel.tests.drag.repository

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val MIN_N_PLAYERS = 5
const val MIN_N_ROUNDS = 1
const val START_ROUND_NUMBER = 1
const val START_TURN_NUMBER = 0

private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yy")

@Entity(tableName = "Lobby")
data class Lobby(

    @ColumnInfo(name = "type")
        var type : LobbyType = LobbyType.LOCAL,

    @ColumnInfo(name ="name")
        var name: String = "",

    @ColumnInfo(name ="maxPlayers" )
        var nPlayers : Int = MIN_N_PLAYERS,

    @ColumnInfo(name = "maxRounds")
        var nRound : Int = MIN_N_ROUNDS,

    @ColumnInfo(name = "state")
        var state : LobbyState = LobbyState.OPEN,

    @PrimaryKey
        @ColumnInfo(name = "id")
        var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "date")
        var date: String = LocalDateTime.now().format(formatter),

    @ColumnInfo(name = "players")
        var players: MutableList<String> = mutableListOf()
)

enum class LobbyType{
    LOCAL, REMOTE
}

enum class LobbyState {
    OPENING, OPEN, FULL, PLAYING, CLOSED
}

@Entity(
    tableName = "Player",
    primaryKeys = ["lobbyId", "name"],
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
        val lobbyId: String = "",

        @ColumnInfo(name = "name")
        val name: String ="",

        @ColumnInfo(name = "type")
        val type: PlayerType = PlayerType.NORMAL,

        @ColumnInfo(name = "state")
        val state: PlayerState = PlayerState.NOT_READY
)

enum class PlayerType {
    OWNER, NORMAL
}

enum class PlayerState{
    READY, NOT_READY
}


@Entity(
        tableName = "Game",
        primaryKeys = ["lobbyId", "date"],
        foreignKeys = [
            ForeignKey(
                    onDelete = CASCADE,
                    childColumns = ["lobbyId"],
                    parentColumns = ["id"],
                    entity = Lobby::class
            )
        ],
        indices = [
                Index("lobbyId", unique = true),
                Index("date", unique = true)
        ]
)
data class Game(
        @ColumnInfo(name = "lobbyId")
        val lobbyId: String = "",

        @ColumnInfo(name = "state")
        val state: GameState = GameState.FINISHED,

        @ColumnInfo(name = "date")
        val date: LocalDateTime = LocalDateTime.now()
)

enum class GameState{
    ALIVE, FINISHED
}


@Entity(
        tableName = "Round",
        primaryKeys = ["lobbyId", "gameDate", "number"],
        foreignKeys = [
            ForeignKey(
                    onDelete = CASCADE,
                    childColumns = ["lobbyId"],
                    parentColumns = ["id"],
                    entity = Lobby::class
            ),
            ForeignKey(
                    onDelete = CASCADE,
                    childColumns = ["gameDate"],
                    parentColumns = ["date"],
                    entity = Game::class
            )
        ],
        indices = [
            Index("lobbyId", unique = true),
            Index("gameDate", unique = true)
        ]
)
data class Round(
        @ColumnInfo(name = "lobbyId")
        val lobbyId: String = "",

        @ColumnInfo(name = "gameDate")
        val gameDate: LocalDateTime = LocalDateTime.now(),

        @ColumnInfo(name = "order")
        val order: MutableList<String> = mutableListOf(),

        @ColumnInfo(name = "number")
        val number: Int = START_ROUND_NUMBER,

)

data class Turn(
    var lobbyId: String = "",

    var gameDate: LocalDateTime = LocalDateTime.now(),

    var roundNumber: Int = START_ROUND_NUMBER,

    var type: TurnType = TurnType.START_WORD,

    var play: String = "",

    var nTurn: Int = START_TURN_NUMBER
)

enum class TurnType{
    START_WORD, WORD, DRAW
}



