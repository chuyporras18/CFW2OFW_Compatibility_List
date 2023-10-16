package com.lightappsdev.cfw2ofwcompatibilitylist.activity.game_details.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.interfaces.GameAdapterListener
import com.lightappsdev.cfw2ofwcompatibilitylist.addForegroundRipple
import com.lightappsdev.cfw2ofwcompatibilitylist.basicDiffUtil
import com.lightappsdev.cfw2ofwcompatibilitylist.databinding.GameImageModelBinding
import com.lightappsdev.cfw2ofwcompatibilitylist.load

class GameImagesAdapter(var listener: GameAdapterListener, var fragmentManager: FragmentManager) :
    RecyclerView.Adapter<GameImagesAdapter.ViewHolder>() {

    var list: List<String> by basicDiffUtil(areItemsTheSame = { old, new -> old == new })

    inner class ViewHolder(private val binding: GameImageModelBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.cardView.setOnClickListener(this)
        }

        fun bind(image: String) {
            binding.cardView.apply {
                addForegroundRipple()
            }

            binding.imageView.load(image)
        }

        override fun onClick(v: View) {
            if (bindingAdapterPosition == RecyclerView.NO_POSITION) return

            val image = list[bindingAdapterPosition]
            when (v) {
                binding.cardView -> listener.onClickImage(image, fragmentManager)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val gameImageModelBinding =
            GameImageModelBinding.inflate(layoutInflater, parent, false).also {
                val minDim = minOf(parent.width, parent.height)

                it.root.layoutParams.width = minDim
                it.root.layoutParams.height = minDim
            }
        return ViewHolder(gameImageModelBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}