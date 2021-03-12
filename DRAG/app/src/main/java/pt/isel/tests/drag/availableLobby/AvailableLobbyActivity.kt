package pt.isel.tests.drag.availableLobby

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pt.isel.tests.drag.R
import pt.isel.tests.drag.setupGame.SetupGameActivity

class AvailableLobbyActivity : AppCompatActivity() {

    companion object{
        fun newIntent(context: Context) = Intent(context, AvailableLobbyActivity::class.java)
    }

    private val lobbyList by lazy{findViewById<RecyclerView>(R.id.lobby_list)}
    private val model by viewModels<AvailableLobbyViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_available_lobby)

        val lobbyAdapter = AvailableLobbyAdapter(this)
        lobbyList.adapter = lobbyAdapter
        lobbyList.layoutManager = LinearLayoutManager(this)

        //lobbyAdapter.lobbyList = listOf("hello", "there")
    }


    fun createLobby(view: View) = startActivity(SetupGameActivity.onlineSetupGame(this))



}