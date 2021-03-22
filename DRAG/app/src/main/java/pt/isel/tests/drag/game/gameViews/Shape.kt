package pt.isel.tests.drag.game.gameViews

import android.graphics.Canvas
import android.graphics.Paint

interface Shape {
    fun draw(canvas: Canvas)
}

data class Point(val x: Float, val y: Float,  val color: Int, val strokeWidth: Float)
    : Shape {
    override fun draw(canvas: Canvas) {
        canvas.drawCircle(
                x*canvas.width,
                y*canvas.height,
                strokeWidth/2,
                ink(color, Paint.Style.FILL_AND_STROKE, 0F)
        )
    }
}

data class Line(private val begin: Point, private val end: Point) : Shape {
    override fun draw(canvas: Canvas) {
        begin.draw(canvas)
        end.draw(canvas)
        canvas.drawLine(
                begin.x * canvas.width,
                begin.y * canvas.height,
                end.x * canvas.width,
                end.y * canvas.height,
                ink(begin.color, Paint.Style.FILL, begin.strokeWidth)
        )
    }
}

fun ink(color: Int, style: Paint.Style, strokeWidth: Float): Paint{
    val paint = Paint()
    paint.color = color
    paint.style = style
    paint.strokeWidth = strokeWidth
    return paint
}