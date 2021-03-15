package pt.isel.tests.drag.repository.converters

import androidx.room.TypeConverter
import pt.isel.tests.drag.repository.LobbyState
import pt.isel.tests.drag.repository.LobbyType
import pt.isel.tests.drag.repository.PlayerType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val SEPARATOR ="/"

class Converters {
    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yy")

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
    fun fromLocalDateTime(date: LocalDateTime) : String = date.format(formatter)

    @TypeConverter
    fun toLocalDate(date: String) : LocalDateTime = LocalDateTime.parse(date, formatter)

    @TypeConverter
    fun toList(players: String) : List<String> = players.split(SEPARATOR)

    @TypeConverter
    fun fromList(players: List<String>):String =
        if(players.isNotEmpty())
            players.reduce {acc, next -> acc + SEPARATOR + next}
        else
            ""

}