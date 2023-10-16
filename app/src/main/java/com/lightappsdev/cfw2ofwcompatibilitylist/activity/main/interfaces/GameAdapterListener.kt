package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.interfaces

import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.game_details.view.GameDetailsActivity
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameModel
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.fullscreen_cover.view.FragmentFullscreenCover

interface GameAdapterListener {

    fun onClick(gameModel: GameModel, context: Context) {
        context.startActivity(Intent(context, GameDetailsActivity::class.java).apply {
            putExtra("id", gameModel.id)
        })
    }

    fun onClickImage(image: String?, fragmentManager: FragmentManager) {
        FragmentFullscreenCover().also { fragment ->
            fragment.arguments = bundleOf("image" to image)
        }.show(fragmentManager, FragmentFullscreenCover.TAG)
    }

    fun onClickHeader() {}
}