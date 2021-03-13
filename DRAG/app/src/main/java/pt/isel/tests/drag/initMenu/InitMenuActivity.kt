package pt.isel.tests.drag.initMenu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pt.isel.tests.drag.availableLobby.AvailableLobbyActivity
import pt.isel.tests.drag.databinding.ActivityInitMenuBinding
import pt.isel.tests.drag.setupLobby.SetupLobbyActivity

class InitMenuActivity : AppCompatActivity() {

    companion object{
        fun newIntent(context: Context) = Intent(context, InitMenuActivity::class.java)
    }

    private val view by lazy {ActivityInitMenuBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
    }

    fun offlineMode(view: View) = startActivity(SetupLobbyActivity.localSetupGame(this))

    fun onlineMode(view: View) = startActivity(AvailableLobbyActivity.newIntent(this))
}