package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums.GameAdapterTypes
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.interfaces.GameAdapterListener
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameAdapterModel
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameHeaderModel
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameModel
import com.lightappsdev.cfw2ofwcompatibilitylist.addBackgroundRipple
import com.lightappsdev.cfw2ofwcompatibilitylist.addForegroundRipple
import com.lightappsdev.cfw2ofwcompatibilitylist.basicDiffUtil
import com.lightappsdev.cfw2ofwcompatibilitylist.databinding.GameModelBinding
import com.lightappsdev.cfw2ofwcompatibilitylist.databinding.HeaderModelBinding
import com.lightappsdev.cfw2ofwcompatibilitylist.load

class GameAdapter(var listener: GameAdapterListener, var fragmentManager: FragmentManager) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: List<GameAdapterModel> by basicDiffUtil(
        areItemsTheSame = { old, new ->
            when {
                old is GameHeaderModel && new is GameHeaderModel -> old.header == new.header
                old is GameModel && new is GameModel -> old.id == new.id
                else -> false
            }
        }
    )

    inner class HeaderViewHolder(private val binding: HeaderModelBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.headerTV.setOnClickListener(this)
        }

        fun bind(headerModel: GameHeaderModel) {
            binding.headerTV.apply {
                addBackgroundRipple()
                text = headerModel.header
            }
        }

        override fun onClick(v: View?) {
            if (bindingAdapterPosition == RecyclerView.NO_POSITION) return

            when (v) {
                binding.headerTV -> {
                    listener.onClickHeader()
                }
            }
        }
    }

    inner class GameViewHolder(private val binding: GameModelBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.cardView.setOnClickListener(this)
            binding.gameCoverCV.setOnClickListener(this)
        }

        fun bind(gameModel: GameModel) {
            binding.gameCoverIV.load(gameModel.images.firstOrNull())

            binding.cardView.addForegroundRipple()
            binding.gameCoverCV.addForegroundRipple()

            binding.gamePlatinumIV.isVisible = gameModel.platinum

            binding.gameTitleTV.apply {
                text = gameModel.title
            }

            binding.chipGroup.removeAllViews()
            gameModel.ids.forEach { gameIdModel ->
                binding.chipGroup.addView(Chip(binding.root.context).apply {
                    text = gameIdModel.id
                })
            }
        }

        override fun onClick(v: View) {
            if (bindingAdapterPosition == RecyclerView.NO_POSITION) return

            val gameModel = list[bindingAdapterPosition] as GameModel
            when (v) {
                binding.cardView -> listener.onClick(gameModel, binding.root.context)
                binding.gameCoverCV ->
                    listener.onClickImage(gameModel.images.firstOrNull(), fragmentManager)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (GameAdapterTypes.values()[viewType]) {
            GameAdapterTypes.HEADER ->
                HeaderViewHolder(HeaderModelBinding.inflate(layoutInflater, parent, false))

            GameAdapterTypes.GAME ->
                GameViewHolder(GameModelBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(list[position] as GameHeaderModel)
            is GameViewHolder -> holder.bind(list[position] as GameModel)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType.ordinal
    }
}