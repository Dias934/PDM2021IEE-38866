package pt.isel.tests.drag.repository.entities

const val MIN_N_PLAYERS = 5
const val MIN_N_ROUNDS = 1
const val START_ROUND_NUMBER = 1
const val START_TURN_NUMBER = 0

interface ILobby {
    var type: LobbyType
    var name: String
    var nPlayers: Int
    var nRound: Int
    var state: LobbyState
    var id: String
    var date: String
    var players: MutableList<String>
}

enum class LobbyType{
    LOCAL, REMOTE
}

enum class LobbyState {
    OPENING, OPEN, FULL, PLAYING, CLOSED
}

interface IPlayer{
    val lobbyId: String
    val name: String
    val type: PlayerType
    val state: PlayerState
}

enum class PlayerType {
    OWNER, NORMAL
}

enum class PlayerState{
    READY, NOT_READY
}

interface IGame{
    val lobbyId: String
    val id: String
    val state: GameState
    val date: String
}

enum class GameState{
    CREATING, ALIVE, FINISHED
}

interface IRound{
    val lobbyId: String
    val gameId: String
    val order: MutableList<String>
    val number: Int
}


