package pt.isel.tests.drag.game.gameViews

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    lateinit var shapes: List<Shape>
    var isVisible = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(isVisible){
            shapes.forEach { it.draw(canvas) }
        }
    }

    fun redraw() {
        invalidate()
    }
}