package pt.isel.tests.drag.repository.converters

import androidx.room.TypeConverter
import pt.isel.tests.drag.repository.entities.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val SEPARATOR ="/"
val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yy")
class Converters {

    companion object{
        fun fromLocalDateTime(date: LocalDateTime) : String = date.format(formatter)

        fun toLocalDateTime(date: String) : LocalDateTime = LocalDateTime.parse(date, formatter)
    }

    @TypeConverter
    fun toLobbyState(state : String) : LobbyState = LobbyState.valueOf(state)

    @TypeConverter
    fun fromLobbyState(state: LobbyState) : String = state.name

    @TypeConverter
    fun toLobbyType(type : String) : LobbyType = LobbyType.valueOf(type)

    @TypeConverter
    fun fromLobbyType(type: LobbyType) : String = type.name

    @TypeConverter
    fun toPlayerType(type : String) : PlayerType = PlayerType.valueOf(type)

    @TypeConverter
    fun fromPlayerType(type: PlayerType) : String = type.name

    @TypeConverter
    fun toPlayerState(state : String) : PlayerState = PlayerState.valueOf(state)

    @TypeConverter
    fun fromPlayerState(state: PlayerState) : String = state.name

    @TypeConverter
    fun toGameState(state : String) : GameState = GameState.valueOf(state)

    @TypeConverter
    fun fromGamerState(state: GameState) : String = state.name

    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime) : String = Converters.fromLocalDateTime(date)

    @TypeConverter
    fun toLocalDateTime(date: String) : LocalDateTime = Converters.toLocalDateTime(date)

    @TypeConverter
    fun toList(players: String) : List<String> = players.split(SEPARATOR)

    @TypeConverter
    fun fromList(players: List<String>):String =
        if(players.isNotEmpty())
            players.reduce {acc, next -> acc + SEPARATOR + next}
        else
            ""

}