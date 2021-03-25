package pt.isel.tests.drag.repository.entities

import pt.isel.tests.drag.repository.converters.formatter
import java.time.LocalDateTime
import java.util.*

data class RemotePlayer(
        override val lobbyId: String = "",

        override val name: String = "",

        override val type: PlayerType= PlayerType.NORMAL,

        override val state: PlayerState = PlayerState.NOT_READY,

        val docRef: String = UUID.randomUUID().toString()
) : IPlayer

data class RemoteRound(
        override val lobbyId: String = "",

        override val gameId: String = "",

        override val order: MutableList<String> = mutableListOf(),

        override val number: Int = START_ROUND_NUMBER,

        val docRef: String = UUID.randomUUID().toString(),
): IRound