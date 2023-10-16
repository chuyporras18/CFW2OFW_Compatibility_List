package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.select_header_to_scroll.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.select_header_to_scroll.listeners.SelectHeaderToScrollAdapterListener
import com.lightappsdev.cfw2ofwcompatibilitylist.addForegroundRipple
import com.lightappsdev.cfw2ofwcompatibilitylist.basicDiffUtil
import com.lightappsdev.cfw2ofwcompatibilitylist.databinding.SelectHeaderModelBinding

class SelectHeaderToScrollAdapter(var listener: SelectHeaderToScrollAdapterListener) :
    RecyclerView.Adapter<SelectHeaderToScrollAdapter.ViewHolder>() {

    var list: List<String> by basicDiffUtil(areItemsTheSame = { old, new -> old == new })

    inner class ViewHolder(private val binding: SelectHeaderModelBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.cardView.setOnClickListener(this)
        }

        fun bind(s: String) {
            binding.cardView.apply {
                addForegroundRipple()
            }

            binding.headerTV.text = s
        }

        override fun onClick(v: View) {
            if (bindingAdapterPosition == RecyclerView.NO_POSITION) return

            val s = list[bindingAdapterPosition]
            when (v) {
                binding.cardView -> listener.onClick(s)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val selectHeaderToScrollBinding =
            SelectHeaderModelBinding.inflate(layoutInflater, parent, false).also {
                val minDim = minOf(parent.width, parent.height)

                it.root.layoutParams.width = minDim / 4
                it.root.layoutParams.height = minDim / 4
            }
        return ViewHolder(selectHeaderToScrollBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}