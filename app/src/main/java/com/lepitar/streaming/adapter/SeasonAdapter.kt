package com.lepitar.streaming.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.lepitar.streaming.MainActivity
import com.lepitar.streaming.R
import com.lepitar.streaming.model.SeasonData

class SeasonAdapter(
    private val context: Context,
    private val list: ArrayList<SeasonData>,
    private val java: MainActivity
): RecyclerView.Adapter<SeasonAdapter.ViewHolder>() {
    var selectedItemPos = 0
    var lastItemSelectedPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.seasonitem_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == selectedItemPos) {
            holder.season_background.setCardBackgroundColor(Color.WHITE)
            holder.season_title.setTextColor(Color.BLACK)
            holder.season_title.text = list[position].text
        } else {
            holder.season_background.setCardBackgroundColor(context.getColor(R.color.black_111))
            holder.season_title.setTextColor(Color.WHITE)
            holder.season_title.text = list[position].text
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val season_title: TextView
        val season_background: CardView

        init {
            season_title = view.findViewById(R.id.season_title)
            season_background = view.findViewById(R.id.season_background)
            season_background.setOnClickListener {
                selectedItemPos = absoluteAdapterPosition
                notifyItemChanged(lastItemSelectedPos)
                lastItemSelectedPos = selectedItemPos
                notifyItemChanged(selectedItemPos)
                java.reloadEpisode(selectedItemPos)
            }
        }

    }

}