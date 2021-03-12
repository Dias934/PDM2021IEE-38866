package pt.isel.tests.drag.setupGame

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import pt.isel.tests.drag.R
import pt.isel.tests.drag.databinding.ActivitySetupGameBinding

const val TAG = "SetupGameActivity"

class SetupGameActivity : AppCompatActivity() {

    companion object{

        fun onlineSetupGame(context: Context) = newIntent(context, GameModes.ONLINE)
        fun offlineSetupGame(context: Context) = newIntent(context, GameModes.OFFLINE)

        private fun newIntent(context: Context, mode: GameModes) = Intent(context, SetupGameActivity::class.java)
            .apply { putExtra("mode", mode) }
    }

    private val views by lazy {ActivitySetupGameBinding.inflate(layoutInflater)}
    private val model by viewModels<SetupGameViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)

        if(model.gameMode == GameModes.OFFLINE){
            views.currentGameMode.setText(R.string.offline_value)
        }
    }
}