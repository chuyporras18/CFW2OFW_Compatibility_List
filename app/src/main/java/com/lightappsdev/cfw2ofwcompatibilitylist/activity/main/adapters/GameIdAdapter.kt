package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameIdModel
import com.lightappsdev.cfw2ofwcompatibilitylist.basicDiffUtil
import com.lightappsdev.cfw2ofwcompatibilitylist.databinding.GameIdModelBinding

class GameIdAdapter : RecyclerView.Adapter<GameIdAdapter.ViewHolder>() {

    var list: List<GameIdModel> by basicDiffUtil(areItemsTheSame = { old, new -> old.id == new.id })

    inner class ViewHolder(private val binding: GameIdModelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(gameIdModel: GameIdModel) {
            binding.gameCountryIdTV.text = gameIdModel.id

            ContextCompat.getDrawable(binding.root.context, gameIdModel.getDtuIcon())
                .let { dtuDrawable -> binding.worksDtuIV.setImageDrawable(dtuDrawable) }

            ContextCompat.getDrawable(binding.root.context, gameIdModel.getHanIcon())
                .let { hanDrawable -> binding.worksHanIV.setImageDrawable(hanDrawable) }

            binding.notesTV.text = gameIdModel.notes
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val gameIdModelBinding = GameIdModelBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(gameIdModelBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}