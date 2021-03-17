package pt.isel.tests.drag.lobby

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.tests.drag.R
import pt.isel.tests.drag.databinding.ActivityLobbyBinding
import pt.isel.tests.drag.repository.LobbyType
import pt.isel.tests.drag.repository.PlayerState
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
        setPlayerListHeader()

        val lobbyAdapter = LobbyAdapter(this)
        playersListView.adapter = lobbyAdapter
        playersListView.layoutManager = LinearLayoutManager(this)

        model.lobby.observe(this, {
            views.lobbyNameTitle.text = getString(R.string.lobby_name_title_string).format(it.name)
            views.lobbyRoundsTitle.text = getString(R.string.rounds_title).format(it.nRound)
            if(it.type == LobbyType.LOCAL)
                views.lobbyActionButton.setText(R.string.start_string)
        })

        model.player.observe(this, { player ->
            if(model.lobbyType == LobbyType.REMOTE){
                lobbyAdapter.currentPlayer = player
                model.currentPlayer = player
            }
        })

        model.players.observe(this, {
            views.playerListHeader.playerStateView.text = getString(R.string.player_state_string)
                    .format(it.filter{player -> player.state == PlayerState.READY}.size,
                            model.currentLobby.nPlayers)
            lobbyAdapter.playersList = it.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, {player -> player.name}))
        })

        lobbyAdapter.newNamePlayer.observe(this, {
            model.updatePlayerName(it)
        })
    }

    private fun setPlayerListHeader(){
        views.playerListHeader.playerActionButton.visibility = View.INVISIBLE
        textViewHeaderSet(views.playerListHeader.playerNameView)
        textViewHeaderSet(views.playerListHeader.playerStateView)
    }

    private fun textViewHeaderSet(text: TextView){
        text.setTextColor(resources.getColor(R.color.white,null))
    }
}