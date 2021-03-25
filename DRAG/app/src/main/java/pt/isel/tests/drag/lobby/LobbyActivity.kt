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
import pt.isel.tests.drag.lobby.lobbyLogic.RemoteLobbyLogic
import pt.isel.tests.drag.setupLobby.LOBBY_TYPE
import pt.isel.tests.drag.observeOnce
import pt.isel.tests.drag.repository.RepositoryResponse
import pt.isel.tests.drag.repository.entities.*


const val LOBBY_ID ="lobby"
const val PLAYER_REF = "player"
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
                    putExtra(PLAYER_REF, playerName)
                }
    }

    private val model by viewModels<LobbyViewModel>()
    private val views by lazy{ActivityLobbyBinding.inflate(layoutInflater)}
    private val playersListView by lazy { views.playerList }
    private val lobbyAdapter: LobbyAdapter by lazy { LobbyAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        setPlayerListHeader()

        playersListView.adapter = lobbyAdapter
        playersListView.layoutManager = LinearLayoutManager(this)

        model.lobby.observe(this, {
            handleLobby(it)
        })

        model.lobbyLogic.players.observe(this){ playerList ->
            handlePlayerList(playerList)
            if(model.lobbyType == LobbyType.REMOTE){
                with(model.lobbyLogic as RemoteLobbyLogic){
                    handleRemotePlayerList(this, playerList as List<RemotePlayer>)
                }
            }
        }

        lobbyAdapter.newNamePlayer.observe(this, {
            model.lobbyLogic.renamePlayer(it.oldName, it.newName)
                    .observeOnce{resp ->
                        if(resp != RepositoryResponse.OK){
                            val txtId = when(resp){
                                RepositoryResponse.NOT_FOUND -> ""
                                RepositoryResponse.ALREADY_EXISTS -> ""
                                else -> ""
                            }
                            Toast.makeText(this, txtId, Toast.LENGTH_SHORT).show()
                        }
                    }
        })

        if(model.lobbyType == LobbyType.REMOTE){
            with(model.lobbyLogic as RemoteLobbyLogic){
                this.livePlayer.observe(this@LobbyActivity){ player ->
                    if(player == null){
                        Toast.makeText(this@LobbyActivity, "kicked out", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else
                        handleRemoteLivePlayer(this, player)
                }

                lobbyAdapter.removePlayer.observe(this@LobbyActivity, { player ->
                    handleRemovePlayer(this, player as RemotePlayer)
                })
            }
        }
    }

    private fun handleLobby(lobby: Lobby){
        views.lobbyNameTitle.text = getString(R.string.lobby_name_title_string).format(lobby.name)
        views.lobbyRoundsTitle.text = getString(R.string.rounds_title).format(lobby.nRound)
        if(lobby.type == LobbyType.LOCAL)
            views.lobbyActionButton.setText(R.string.start_string)
        else if(lobby.state == LobbyState.PLAYING){
            with(model.lobbyLogic as RemoteLobbyLogic){
                this.createGame().observe(this@LobbyActivity){ gameId ->
                    startActivity(GameActivity.startRemoteGame(this@LobbyActivity,
                            this.currentLobby.id,
                            this.currentPlayer.name,
                            gameId))
                }
            }
        }
    }

    private fun handlePlayerList(players: List<IPlayer>){
        views.playerListHeader.playerStateView.text = getString(R.string.player_state_string)
                .format(players.filter{player -> player.state == PlayerState.READY}.size,
                        model.lobbyLogic.currentLobby.nPlayers)
        lobbyAdapter.playersList = players
                .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { player -> player.name}))
    }

    private fun handleRemovePlayer(lobbyLogic: RemoteLobbyLogic, player: RemotePlayer){
        lobbyLogic.removePlayer(player)
                .observeOnce{resp ->
                    if(resp != RepositoryResponse.OK){
                        val txtId = when(resp){
                            RepositoryResponse.NOT_FOUND -> ""
                            RepositoryResponse.ALREADY_EXISTS -> ""
                            else -> ""
                        }
                        Toast.makeText(this, txtId, Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun handleRemoteLivePlayer(lobbyLogic: RemoteLobbyLogic, player: RemotePlayer){
        lobbyAdapter.setCurrentPlayer(player)
        if (player.type == PlayerType.OWNER) {
            views.lobbyActionButton.setText(R.string.start_string)
        }
        else{
            views.lobbyActionButton.setText(
                    if(player.state == PlayerState.READY) R.string.ready_string
                    else R.string.not_ready_string)
        }
    }

    private fun handleRemotePlayerList(lobbyLogic: RemoteLobbyLogic, players: List<RemotePlayer>){
        if(lobbyLogic.isCurrentPlayerOwner()){
            views.lobbyActionButton.isEnabled =
                    players.filter { p -> p.state == PlayerState.READY }
                            .size == lobbyLogic.currentLobby.nPlayers
        }
    }

    fun lobbyActionButtonClickEvent( view: View){
        if(model.lobbyType == LobbyType.REMOTE){
            with(model.lobbyLogic as RemoteLobbyLogic){
                if(this.isCurrentPlayerOwner()){
                    this.createGame().observe(this@LobbyActivity){ gameId ->
                        startActivity(GameActivity.startRemoteGame(this@LobbyActivity,
                                this.currentLobby.id,
                                this.currentPlayer.name,
                                gameId))
                    }
                }
                else{
                    this.changePlayerState(
                            if(this.currentPlayer.state == PlayerState.READY)
                                PlayerState.NOT_READY
                            else
                                PlayerState.READY)
                            .observeOnce{resp ->
                                if(resp != RepositoryResponse.OK){
                                    val txtId = when(resp){
                                        RepositoryResponse.NOT_FOUND -> ""
                                        RepositoryResponse.ALREADY_EXISTS -> ""
                                        else -> ""
                                    }
                                    Toast.makeText(this@LobbyActivity, txtId, Toast.LENGTH_SHORT).show()
                                }
                            }
                }
            }

        }
        else{
            model.lobbyLogic.createGame().observe(this){gameId ->
                startActivity(GameActivity.startLocalGame(this, model.lobbyId, gameId))
            }
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

