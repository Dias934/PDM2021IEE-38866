package pt.isel.tests.drag.game

import android.content.Context
import android.view.View
import android.widget.AdapterView
import androidx.core.content.res.getResourceIdOrThrow
import pt.isel.tests.drag.R
import pt.isel.tests.drag.game.gameViews.ISpinnerAdapter
import pt.isel.tests.drag.game.gameViews.ISpinnerItem

class DrawToolsAdapter(val context: Context) {

    var widthAdapter: ISpinnerAdapter<Float>
    var colorAdapter: ISpinnerAdapter<Int>

    init {
        widthAdapter = ISpinnerAdapter(context, getWidths())
        colorAdapter = ISpinnerAdapter(context, getColors())
    }

    private fun getColors(): List<ColorItem> {
        val colorIds = context.resources.obtainTypedArray(R.array.color_array)
        val colors = mutableListOf<ColorItem>()
        for(i in 0 until colorIds.length()){
            colors.add(ColorItem(colorIds.getResourceId(i, 0), colorIds.getColor(i,0) ))
        }
        return colors
    }

    private fun getWidths(): List<WidthItem> {
        val widthImgs = context.resources.obtainTypedArray(R.array.width_drawables)
        val widthValues = context.resources.obtainTypedArray(R.array.width_array)
        val widths = mutableListOf<WidthItem>()
        for (i in 0 until widthValues.length()){
            widths.add(WidthItem(widthImgs.getResourceId(i,0), widthValues.getFloat(i,0F)))
        }
        return widths
    }

}

class DrawItemSelector<T>(val action: (T) -> Unit) : AdapterView.OnItemSelectedListener {

     override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(parent != null){
            val item = parent.getItemAtPosition(position)
            action(item as T)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}


data class WidthItem (override val img: Int, override val value: Float) : ISpinnerItem<Float>

data class ColorItem (override val img: Int, override val value: Int) : ISpinnerItem<Int>