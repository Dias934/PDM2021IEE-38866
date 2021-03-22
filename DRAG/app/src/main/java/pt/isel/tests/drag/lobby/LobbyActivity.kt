package pt.isel.tests.drag.lobby

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.tests.drag.R
import pt.isel.tests.drag.databinding.ActivityLobbyBinding
import pt.isel.tests.drag.game.GameActivity
import pt.isel.tests.drag.repository.LobbyType
import pt.isel.tests.drag.repository.PlayerState
import pt.isel.tests.drag.repository.PlayerType
import pt.isel.tests.drag.setupLobby.LOBBY_TYPE
import pt.isel.tests.drag.observeOnce


const val LOBBY_ID ="lobby"
const val PLAYER_NAME = "player"
class LobbyActivity : AppCompatActivity() {

    companion object{
        fun localLobby(context: Context, lobbyId: String) : Intent =
                Intent(context, LobbyActivity::class.java).apply {
                    putExtra(LOBBY_TYPE, LobbyType.LOCAL)
                    putExtra(LOBBY_ID, lobbyId)
                }

        fun remoteLobby(context: Context, lobbyId: String, playerName: String) : Intent =
                Intent(context, LobbyActivity::class.java).apply {
                    putExtra(LOBBY_TYPE, LobbyType.REMOTE)
                    putExtra(LOBBY_ID, lobbyId)
                    putExtra(PLAYER_NAME, playerName)
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
            if(player == null){
                Toast.makeText(this@LobbyActivity, "kicked out", Toast.LENGTH_SHORT).show()
                finish()
            }
            else if(model.lobbyType == LobbyType.REMOTE) {
                lobbyAdapter.currentPlayer = player
                model.currentPlayer = player
                if (player.type == PlayerType.OWNER) {
                    views.lobbyActionButton.setText(R.string.start_string)
                }
                else{
                    views.lobbyActionButton.setText(
                            if(player.state == PlayerState.READY) R.string.ready_string
                            else R.string.not_ready_string)
                }
            }
        })

        model.players.observe(this, { playerList ->
            if(model.isCurrentPlayerInitialized() && !playerList.contains(model.currentPlayer)){
                Toast.makeText(this@LobbyActivity, "kicked out", Toast.LENGTH_SHORT).show()
                finish()
            }
            views.playerListHeader.playerStateView.text = getString(R.string.player_state_string)
                    .format(playerList.filter{player -> player.state == PlayerState.READY}.size,
                            model.currentLobby.nPlayers)
            lobbyAdapter.playersList = playerList
                    .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { player -> player.name}))

            if(model.lobbyType == LobbyType.REMOTE ){
                if( model.currentPlayer.type == PlayerType.OWNER) {
                    views.lobbyActionButton.isEnabled =
                            playerList
                                    .filter { p -> p.state == PlayerState.READY }
                                    .size == model.currentLobby.nPlayers
                }
                else{
                    startActivity(GameActivity.startRemoteGame(this,
                            model.currentLobby.id,
                            model.currentPlayer.name,
                            ""))
                }
            }
        })

        lobbyAdapter.newNamePlayer.observe(this, {
            model.renamePlayer(it.oldName, it.newName)
                    .observeOnce{resp ->
                        if(!resp)
                            Toast.makeText(this, getString(R.string.rename_operation_error_string), Toast.LENGTH_SHORT).show()
                    }
        })

        lobbyAdapter.removePlayer.observe(this, {
            model.removePlayer(it)
        })
    }

    fun lobbyActionButtonClickEvent( view: View){
        if(model.currentLobby.type == LobbyType.REMOTE){
            if(model.currentPlayer.type == PlayerType.OWNER){
                startActivity(GameActivity.startRemoteGame(this,
                        model.currentLobby.id,
                        model.currentPlayer.name,
                        ""))
            }
            else{
                model.changePlayerState(
                        if(model.currentPlayer.state == PlayerState.READY)
                            PlayerState.NOT_READY
                        else
                            PlayerState.READY)
            }
        }
        else{
            startActivity(GameActivity.startLocalGame(this, model.currentLobby.id, ""))
        }
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

