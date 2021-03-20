package pt.isel.tests.drag.setupLobby

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import pt.isel.tests.drag.R
import pt.isel.tests.drag.databinding.ActivitySetupLobbyBinding
import pt.isel.tests.drag.lobby.LobbyActivity
import pt.isel.tests.drag.repository.LobbyType

const val TAG = "SetupGameActivity"
const val LOBBY_TYPE = "type"

class SetupLobbyActivity : AppCompatActivity() {

    companion object{

        fun remoteSetupGame(context: Context) = newIntent(context, LobbyType.REMOTE)
        fun localSetupGame(context: Context) = newIntent(context, LobbyType.LOCAL)

        private fun newIntent(context: Context, type: LobbyType) = Intent(context, SetupLobbyActivity::class.java)
            .apply { putExtra(LOBBY_TYPE, type) }
    }

    private val views by lazy { ActivitySetupLobbyBinding.inflate(layoutInflater)}
    private val model by viewModels<SetupLobbyViewModel>()

    private val minPlayers by lazy{resources.getInteger(R.integer.min_players)}
    private val minRounds by lazy{resources.getInteger(R.integer.min_rounds)}
    private val minChars by lazy{resources.getInteger(R.integer.min_chars)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)

        if(model.lobbyType == LobbyType.LOCAL){
            views.currentGameMode.setText(R.string.local_value)
        }
    }

    fun createLobby(view: View){
        val (players, rounds, name) = getValidInputs()
        if(players != null && rounds != null && name != null){
            model.createLobby(name, players, rounds, getString(R.string.player_value))
            model.lobbyId.observe(this, { lobbyId ->
                if(model.lobbyType == LobbyType.LOCAL)
                    startActivity(LobbyActivity.localLobby(this, lobbyId))
                else{
                    model.getPlayerOwner(lobbyId)
                    model.player.observe(this){ player ->
                        startActivity(LobbyActivity.remoteLobby(this, lobbyId, player.id))
                    }
                }
            })
        }
    }

    private fun getNumPlayers(): Int ? =
            views.nPlayersInput.text.toString().trim().toIntOrNull().takeIf{it!=null && it >=minPlayers }

    private fun getNumRounds(): Int ? =
            views.nRoundsInput.text.toString().trim().toIntOrNull()
                    .takeIf{it!=null && it >=minRounds}

    private fun getLobbyName(): String ? =
            views.lobbyNameInput.text.toString().trim()
                    .takeIf{it.length >= minChars}

    private fun getValidInputs(): SetupLobbyInput {
        val nPlayers = getNumPlayers()
        val nRounds = getNumRounds()
        val lobbyName = getLobbyName()

        if(nPlayers == null)
            views.nPlayersInput.error = getString(R.string.n_players_error).format(minPlayers)

        if(nRounds == null)
            views.nRoundsInput.error = getString(R.string.n_rounds_error).format(minRounds)

        if(lobbyName == null)
            views.lobbyNameInput.error = getString(R.string.lobby_name_error).format(minChars)

        return SetupLobbyInput(nPlayers,nRounds,lobbyName)
    }

    private data class SetupLobbyInput(val players: Int?, val rounds: Int?, val name: String?)
}