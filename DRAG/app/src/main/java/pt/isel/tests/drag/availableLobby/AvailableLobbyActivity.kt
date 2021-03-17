package pt.isel.tests.drag.availableLobby

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.tests.drag.R
import pt.isel.tests.drag.databinding.ActivityAvailableLobbyBinding
import pt.isel.tests.drag.lobby.LobbyActivity

import pt.isel.tests.drag.repository.Lobby
import pt.isel.tests.drag.repository.LobbyType
import pt.isel.tests.drag.setupLobby.SetupLobbyActivity

class AvailableLobbyActivity : AppCompatActivity() {

    companion object{
        fun newIntent(context: Context) = Intent(context, AvailableLobbyActivity::class.java)
    }


    private val model by viewModels<AvailableLobbyViewModel>()
    private val views by lazy{ ActivityAvailableLobbyBinding.inflate(layoutInflater)}
    private val lobbyList by lazy {views.lobbyList}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        setLobbyListHeader()
        val lobbyAdapter = AvailableLobbyAdapter(this)
        lobbyList.adapter = lobbyAdapter
        lobbyList.layoutManager = LinearLayoutManager(this)

        model.lobbys.observe(this, {
            lobbyAdapter.lobbyList = it
        })

        lobbyAdapter.joinLobby.observe(this, {lobbyId ->
            model.createPlayer(lobbyId)
            model.playerId.observe(this, {playerId ->
                startActivity(LobbyActivity.remoteLobby(this, lobbyId, playerId))
            })
        })
    }


    private fun setLobbyListHeader(){
        views.lobbyListHeader.joinButton.visibility = View.INVISIBLE
        textViewHeaderSet(views.lobbyListHeader.lobbyNameHeader)
        textViewHeaderSet(views.lobbyListHeader.lobbyAvailabilityHeader)
        textViewHeaderSet(views.lobbyListHeader.lobbyRounds)
    }

    private fun textViewHeaderSet(text: TextView){
        text.setTextColor(resources.getColor(R.color.white,null))
    }



    fun goToSetupLobby(view: View) = startActivity(SetupLobbyActivity.remoteSetupGame(this))



}