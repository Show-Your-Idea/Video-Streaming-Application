package com.lepitar.streaming.adapter

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lepitar.streaming.R
import com.lepitar.streaming.VideoPlayer
import com.lepitar.streaming.model.EpisodeData

class EpisodeAdapter(val context: Context, val list: ArrayList<EpisodeData>): RecyclerView.Adapter<EpisodeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.episodeitem_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.episode_title.text = list[position].text
        holder.episode_background.setOnClickListener {
            val intent = Intent(context, VideoPlayer::class.java).apply {
                putExtra("url", list[position].url)
                putExtra("title", list[position].text)
                Log.d("Address", list[position].url)
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val episode_title: TextView
        val episode_background: LinearLayout

        init {
            episode_title = view.findViewById(R.id.episode_title)
            episode_background = view.findViewById(R.id.episode_background)
        }

    }

}