package com.lightappsdev.cfw2ofwcompatibilitylist.activity.game_details.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.game_details.adapters.GameImagesAdapter
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.game_details.viewmodel.FragmentGameDetailsViewModel
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.adapters.GameIdAdapter
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.interfaces.GameAdapterListener
import com.lightappsdev.cfw2ofwcompatibilitylist.addBackgroundCircleRipple
import com.lightappsdev.cfw2ofwcompatibilitylist.addBackgroundRipple
import com.lightappsdev.cfw2ofwcompatibilitylist.databinding.ActivityGameDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameDetailsActivity : AppCompatActivity(), GameAdapterListener {

    private lateinit var binding: ActivityGameDetailsBinding

    private val viewModel: FragmentGameDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameDetailsBinding.inflate(layoutInflater)

        binding.recyclerView.apply {
            adapter = viewModel.gameIdAdapter.value ?: GameIdAdapter().also { adapter ->
                viewModel.gameIdAdapter(adapter)
            }
        }

        val listener = this
        binding.recyclerViewImages.apply {
            adapter = viewModel.gameImagesAdapter.value
                ?: GameImagesAdapter(listener, supportFragmentManager).also { adapter ->
                    viewModel.gameImagesAdapter(adapter)
                }
            setHasFixedSize(true)
        }

        binding.gameTitleTV.apply {
            addBackgroundRipple()
            setOnClickListener {
                viewModel.copyTitle()
            }
        }

        binding.gamePlatinumIV.apply {
            addBackgroundCircleRipple()
            setOnClickListener { viewModel.togglePlatinum() }
        }

        viewModel.gameModel.observe(this) { gameModel ->
            if (gameModel == null) {
                finish()
                return@observe
            }

            binding.gameTitleTV.text = gameModel.title

            viewModel.gameIdAdapter.value?.list = gameModel.ids
            viewModel.gameImagesAdapter.value?.list = gameModel.images

            val drawable =
                ContextCompat.getDrawable(binding.root.context, gameModel.gamePlatinumImage())
            binding.gamePlatinumIV.setImageDrawable(drawable)
        }

        viewModel.getGameData(intent?.getIntExtra("id", -1))

        setContentView(binding.root)
    }
}