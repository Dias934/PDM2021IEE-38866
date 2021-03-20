package pt.isel.tests.drag.game

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import pt.isel.tests.drag.R
import pt.isel.tests.drag.databinding.ActivityGameBinding
import pt.isel.tests.drag.lobby.LOBBY_ID
import pt.isel.tests.drag.lobby.PLAYER_ID
import pt.isel.tests.drag.repository.LobbyType
import pt.isel.tests.drag.setupLobby.LOBBY_TYPE

const val GAME_ID ="game"

class GameActivity : AppCompatActivity() {

    companion object{
        fun newIntent(context: Context, lobbyType: LobbyType, lobbyId: String, playerId: String,
                      gameId: String) =
                Intent(context, GameActivity::class.java).apply {
                    putExtra(LOBBY_TYPE, lobbyType)
                    putExtra(LOBBY_ID, lobbyId)
                    putExtra(PLAYER_ID, playerId)
                    putExtra(GAME_ID, gameId)
                }
    }

    private val views by lazy { ActivityGameBinding.inflate(layoutInflater) }
    private val model by viewModels<GameViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)

        val adapters = DrawToolsAdapter(this)
        views.widthSpinner.adapter = adapters.widthAdapter
        views.widthSpinner.onItemSelectedListener = WidthSelector()
        views.colorSpinner.adapter = adapters.colorAdapter
        views.colorSpinner.onItemSelectedListener = WidthSelector()
    }

    inner class WidthSelector(): AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

    }
}