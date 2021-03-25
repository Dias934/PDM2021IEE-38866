package pt.isel.tests.drag.repository.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import pt.isel.tests.drag.repository.converters.formatter
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "Lobby")
data class Lobby(

        @ColumnInfo(name = "type")
        override var type : LobbyType = LobbyType.LOCAL,

        @ColumnInfo(name ="name")
        override var name: String = "",

        @ColumnInfo(name ="maxPlayers" )
        override var nPlayers : Int = MIN_N_PLAYERS,

        @ColumnInfo(name = "maxRounds")
        override var nRound : Int = MIN_N_ROUNDS,

        @ColumnInfo(name = "state")
        override var state : LobbyState = LobbyState.OPEN,

        @PrimaryKey
        @ColumnInfo(name = "id")
        override var id: String = UUID.randomUUID().toString(),

        @ColumnInfo(name = "date")
        override var date: String = LocalDateTime.now().format(formatter),

        @ColumnInfo(name = "players")
        override var players: MutableList<String> = mutableListOf()
): ILobby

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
data class LocalPlayer(
        @ColumnInfo( name = "lobbyId")
        override val lobbyId: String = "",

        @ColumnInfo(name = "name")
        override val name: String ="",

        @ColumnInfo(name = "type")
        override val type: PlayerType = PlayerType.NORMAL,

        @ColumnInfo(name = "state")
        override val state: PlayerState = PlayerState.NOT_READY
) : IPlayer

@Entity(
        tableName = "Game",
        primaryKeys = ["lobbyId", "id"],
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
            Index("id", unique = true)
        ]
)
data class Game(
        @ColumnInfo(name = "lobbyId")
        override val lobbyId: String = "",

        @ColumnInfo(name = "state")
        override val state: GameState = GameState.CREATING,

        @ColumnInfo(name = "date")
        override val date: String = LocalDateTime.now().format(formatter),

        @ColumnInfo(name = "id")
        override val id: String = UUID.randomUUID().toString()
) : IGame

@Entity(
        tableName = "Round",
        primaryKeys = ["lobbyId", "gameId", "number"],
        foreignKeys = [
            ForeignKey(
                    onDelete = CASCADE,
                    childColumns = ["lobbyId"],
                    parentColumns = ["id"],
                    entity = Lobby::class
            ),
            ForeignKey(
                    onDelete = CASCADE,
                    childColumns = ["gameId"],
                    parentColumns = ["id"],
                    entity = Game::class
            )
        ],
        indices = [
            Index("lobbyId", unique = true),
            Index("gameId", unique = true)
        ]
)
data class LocalRound(
        @ColumnInfo(name = "lobbyId")
        override val lobbyId: String = "",

        @ColumnInfo(name = "gameId")
        override val gameId: String = "",

        @ColumnInfo(name = "order")
        override val order: MutableList<String> = mutableListOf(),

        @ColumnInfo(name = "number")
        override val number: Int = START_ROUND_NUMBER,

        ): IRound

data class Turn(
        var lobbyId: String = "",

        var gameId: String = "",

        var roundNumber: Int = START_ROUND_NUMBER,

        var type: TurnType = TurnType.START_WORD,

        var play: String = "",

        var nTurn: Int = START_TURN_NUMBER
)

enum class TurnType{
    START_WORD, WORD, DRAW
}



