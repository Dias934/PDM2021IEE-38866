package pt.isel.tests.drag.game

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import pt.isel.tests.drag.R
import pt.isel.tests.drag.databinding.ActivityGameBinding
import pt.isel.tests.drag.game.gameViews.Line
import pt.isel.tests.drag.game.gameViews.Point
import pt.isel.tests.drag.game.gameViews.Shape
import pt.isel.tests.drag.lobby.LOBBY_ID
import pt.isel.tests.drag.lobby.PLAYER_NAME
import pt.isel.tests.drag.repository.LobbyType
import pt.isel.tests.drag.setupLobby.LOBBY_TYPE

const val GAME_ID ="game"

class GameActivity : AppCompatActivity() {

    companion object{
        fun startLocalGame(context: Context, lobbyId: String , gameId: String) =
                Intent(context, GameActivity::class.java).apply {
                    putExtra(LOBBY_TYPE, LobbyType.LOCAL)
                    putExtra(LOBBY_ID, lobbyId)
                    putExtra(GAME_ID, gameId)
                }

        fun startRemoteGame (context: Context, lobbyId: String, playerId: String,
                      gameId: String) =
                Intent(context, GameActivity::class.java).apply {
                    putExtra(LOBBY_TYPE, LobbyType.REMOTE)
                    putExtra(LOBBY_ID, lobbyId)
                    putExtra(PLAYER_NAME, playerId)
                    putExtra(GAME_ID, gameId)
                }
    }

    private val views by lazy { ActivityGameBinding.inflate(layoutInflater) }
    private val model by viewModels<GameViewModel>()

    private var widthValue : Float = 0F
    private var colorValue : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)

        setDrawTools()
        setDrawView()


    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setDrawView(){
        views.drawInput.isVisible = true
        var origin: Point? = null
        views.drawInput.shapes = model.getShapes()
        views.drawInput.setOnTouchListener { _, event ->
            val x = event.x / views.drawInput.width
            val y = event.y / views.drawInput.height
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    origin = Point(x, y, colorValue, widthValue )
                }
                MotionEvent.ACTION_MOVE -> {
                    val newPoint = Point(x, y, colorValue, widthValue )
                    origin?.let { saveShape(Line(it, newPoint), false)  }
                    origin = newPoint
                }
                MotionEvent.ACTION_UP -> {
                    val newPoint = Point(x, y, colorValue, widthValue )
                    origin?.let {
                        if (it != newPoint)
                            saveShape(Line(it, newPoint), true)
                        else
                            saveShape(it, true)
                    }
                }

            }
            true
        }
    }

    private fun saveShape(shape: Shape, finishMotion: Boolean) {
        model.addShape(shape)
        if(finishMotion) {
            model.endMotion()
            views.drawInput.redraw()    //always redraw when user lifts finger from the screen
        }
        else if (model.getCurrentMotion().size % 3 == 0){ // redraws while painting but less frequently for performance
            views.drawInput.redraw()
        }
    }

    private fun setDrawTools() {
        val adapters = DrawToolsAdapter(this)

        views.widthSpinner.adapter = adapters.widthAdapter
        views.widthSpinner.onItemSelectedListener = DrawItemSelector { item: WidthItem -> widthValue = item.value }

        views.colorSpinner.adapter = adapters.colorAdapter
        views.colorSpinner.onItemSelectedListener = DrawItemSelector{ item: ColorItem -> colorValue = item.value }
    }

    fun undoButtonAction(view: View){
        model.undo()
        views.drawInput.redraw()
    }

    fun deleteDrawAction(view : View){
        val text = TextView(this)
        text.setText(R.string.delete_draw_text)
        text.textSize = 18F
        AlertDialog.Builder(this)
                .setView(text)
                .setPositiveButton(R.string.confirm_string) { dialog: DialogInterface, _: Int ->
                    model.deleteShapes()
                    views.drawInput.redraw()
                }
                .setNegativeButton(R.string.cancel_string){ dialog: DialogInterface, _: Int ->
                    dialog.cancel()
                }
                .show()
    }




}