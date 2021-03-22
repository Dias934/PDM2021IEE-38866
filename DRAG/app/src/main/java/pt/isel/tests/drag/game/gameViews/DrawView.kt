package pt.isel.tests.drag.game.gameViews

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    lateinit var shapes: MutableList<MutableList<Shape>> //list of list for undo button
    var isVisible = false

    override fun onDraw(canvas: Canvas) {
        if(this::shapes.isInitialized){
            shapes.forEach {it.forEach { shape -> shape.draw(canvas) }}
        }
    }

    fun redraw() {
        invalidate()
    }
}