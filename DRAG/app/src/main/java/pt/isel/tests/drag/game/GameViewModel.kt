package pt.isel.tests.drag.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import pt.isel.tests.drag.game.gameViews.Shape

class GameViewModel(private val app: Application, private val state: SavedStateHandle): AndroidViewModel(app) {

    private val shapes = mutableListOf(mutableListOf<Shape>())

    private var currentMotion: MutableList<Shape> = shapes.last()

    fun addShape(shape: Shape) = currentMotion.add(shape)

    fun endMotion(){
        currentMotion = mutableListOf()
        shapes.add(currentMotion)
    }

    fun getShapes(): MutableList<MutableList<Shape>> = shapes

    fun getCurrentMotion(): MutableList<Shape> = currentMotion

    fun undo() {
        if(shapes.size > 1){
            if(shapes.last().isEmpty())
                shapes.removeAt(shapes.size - 2)
            else {
                shapes.removeAt(shapes.size - 1)
                endMotion()
            }
        }
    }

    fun deleteShapes() {
        shapes.clear()
        endMotion()
    }
}