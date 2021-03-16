package pt.isel.tests.drag.lobby

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.tests.drag.R
import pt.isel.tests.drag.databinding.ActivityLobbyBinding
import pt.isel.tests.drag.repository.LobbyType
import pt.isel.tests.drag.setupLobby.TYPE_FIELD


const val LOBBY_ID ="lobby"
const val PLAYER_ID = "player"
class LobbyActivity : AppCompatActivity() {

    companion object{
        fun localLobby(context: Context, lobbyId: String) : Intent =
                Intent(context, LobbyActivity::class.java).apply {
                    putExtra(TYPE_FIELD, LobbyType.LOCAL)
                    putExtra(LOBBY_ID, lobbyId)
                }

        fun remoteLobby(context: Context, lobbyId: String, playerId: String) : Intent =
                Intent(context, LobbyActivity::class.java).apply {
                    putExtra(TYPE_FIELD, LobbyType.REMOTE)
                    putExtra(LOBBY_ID, lobbyId)
                    putExtra(PLAYER_ID, playerId)
                }
    }

    private val model by viewModels<LobbyViewModel>()
    private val views by lazy{ActivityLobbyBinding.inflate(layoutInflater)}
    private val playersListView by lazy { views.playerList }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)

        val lobbyAdapter = LobbyAdapter(this)
        playersListView.adapter = lobbyAdapter
        playersListView.layoutManager = LinearLayoutManager(this)

        model.lobby.observe(this, {
            views.lobbyNameTitle.text = it.name
            if(it.type == LobbyType.LOCAL)
                views.lobbyActionButton.setText(R.string.start_string)
        })

        model.players.observe(this, {
            lobbyAdapter.playersList = it
        })

        lobbyAdapter.newNamePlayer.observe(this, {
            model.updatePlayerName(it)
        })


    }
}