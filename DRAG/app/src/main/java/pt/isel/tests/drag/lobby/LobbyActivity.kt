package pt.isel.tests.drag.lobby

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isel.tests.drag.R


const val LOBBY_ID ="lobby"
const val PLAYER_ID = "player"
class LobbyActivity : AppCompatActivity() {

    companion object{
        fun localLobby(context: Context, lobbyId: String) : Intent =
                Intent(context, LobbyActivity::class.java).apply { putExtra(LOBBY_ID, lobbyId) }

        fun remoteLobby(context: Context, lobbyId: String, playerId: String) : Intent =
                Intent(context, LobbyActivity::class.java).apply {
                    putExtra(LOBBY_ID, lobbyId)
                    putExtra(PLAYER_ID, playerId)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
    }
}