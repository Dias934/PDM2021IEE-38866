package pt.isel.tests.drag.setupLobby

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import pt.isel.tests.drag.R
import pt.isel.tests.drag.databinding.ActivitySetupLobbyBinding
import pt.isel.tests.drag.repository.LobbyType

const val TAG = "SetupGameActivity"
const val TYPE_FIELD = "type"

class SetupLobbyActivity : AppCompatActivity() {

    companion object{

        fun remoteSetupGame(context: Context) = newIntent(context, LobbyType.REMOTE)
        fun localSetupGame(context: Context) = newIntent(context, LobbyType.LOCAL)

        private fun newIntent(context: Context, type: LobbyType) = Intent(context, SetupLobbyActivity::class.java)
            .apply { putExtra(TYPE_FIELD, type) }
    }

    private val views by lazy {ActivitySetupLobbyBinding.inflate(layoutInflater)}
    private val model by viewModels<SetupLobbyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)

        if(model.lobbyType == LobbyType.LOCAL){
            views.currentGameMode.setText(R.string.local_value)
        }
    }
}