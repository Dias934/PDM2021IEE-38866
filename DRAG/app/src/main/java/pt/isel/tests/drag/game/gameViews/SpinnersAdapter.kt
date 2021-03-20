package pt.isel.tests.drag.game.gameViews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import pt.isel.tests.drag.R

class ISpinnerAdapter<T>(context: Context, widthItem: List<ISpinnerItem<T>>) :
        ArrayAdapter<ISpinnerItem<T>>(context, 0, widthItem) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val retView = convertView ?: LayoutInflater
                .from(context)
                .inflate(R.layout.image_spinner_row, parent, false)

        val img: ImageView = retView.findViewById(R.id.imagem_view)
        val currentItem = getItem(position)
        if (currentItem != null) {
            img.setImageResource(currentItem.img)
        }
        return retView
    }

}
