package pt.isel.tests.drag.setupGame

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle


class SetupGameViewModel(private val app: Application, private val state: SavedStateHandle):
    AndroidViewModel(app) {

        val gameMode: GameModes by lazy { state.get<GameModes>("mode")?: GameModes.OFFLINE }
}

enum class GameModes{ OFFLINE, ONLINE }