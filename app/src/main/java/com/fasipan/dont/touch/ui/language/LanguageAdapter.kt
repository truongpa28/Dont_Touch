package com.fasipan.dont.touch.ui.language

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.model.LanguageModel
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.gone
import com.fasipan.dont.touch.utils.ex.show
import com.google.android.material.card.MaterialCardView

class LanguageAdapter(
    var context: Context,
    private var listData: List<LanguageModel>,
    private val listener: ClickLanguageListener
) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    private var position = 0

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var viewAll: MaterialCardView = view.findViewById(R.id.viewAll)
        var imgSelected: ImageView = view.findViewById(R.id.imgSelected)
        var imgNoSelect: ImageView = view.findViewById(R.id.imgNoSelect)
        var txtName: TextView = view.findViewById(R.id.txtName)
        var imgFlag: ImageView = view.findViewById(R.id.imgFlag)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_language, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val data = listData[position]

        viewHolder.txtName.text = data.name
        viewHolder.imgFlag.setImageResource(data.flag)
        viewHolder.viewAll.clickSafe {
            listener.clickLanguage(position, data)
        }
        if (position == this.position) {
            viewHolder.imgSelected.show()
            viewHolder.imgNoSelect.gone()
        } else {
            viewHolder.imgSelected.gone()
            viewHolder.imgNoSelect.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setChoose(choose: Int) {
        position = choose
        notifyDataSetChanged()
    }

    fun getChoose() = position

    override fun getItemCount() = listData.size

}

interface ClickLanguageListener {
    fun clickLanguage(position: Int, languageModel: LanguageModel)
}