package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.lightappsdev.cfw2ofwcompatibilitylist.R
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.adapters.GameAdapter
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.interfaces.GameAdapterListener
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.model.GameHeaderModel
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.filter_games.view.FragmentFilterGames
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.select_header_to_scroll.view.FragmentSelectHeaderToScroll
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.viewmodel.MainActivityViewModel
import com.lightappsdev.cfw2ofwcompatibilitylist.addBackgroundCircleRipple
import com.lightappsdev.cfw2ofwcompatibilitylist.databinding.ActivityMainBinding
import com.lightappsdev.cfw2ofwcompatibilitylist.restoreState
import com.lightappsdev.cfw2ofwcompatibilitylist.saveState
import com.lightappsdev.cfw2ofwcompatibilitylist.setProgressSmoothly
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), GameAdapterListener {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainActivityViewModel by viewModels()

    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(enabled = true) {
            override fun handleOnBackPressed() {

                if (binding.editText.isFocused) {
                    binding.editText.setText("")
                    binding.editText.clearFocus()
                    binding.textInput.clearFocus()
                    return
                }

                val firstVisibleItemPosition =
                    (binding.recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                if (firstVisibleItemPosition != 0) {
                    binding.recyclerView.smoothScrollToPosition(0)
                    return
                }

                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val listener = this

        binding.recyclerView.apply {
            adapter = viewModel.gameAdapter.value?.also { adapter ->
                adapter.listener = listener
                adapter.fragmentManager = supportFragmentManager
            } ?: GameAdapter(listener, supportFragmentManager).also { adapter ->
                viewModel.gameListAdapter(adapter)
            }
            setHasFixedSize(true)
            restoreState(savedInstanceState)
        }

        binding.toolbar.apply {
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.updateGamesMenu -> viewModel.updateGames()
                    R.id.backupGamesMenu -> viewModel.backupGames()
                }
                return@setOnMenuItemClickListener true
            }
        }

        binding.editText.addTextChangedListener {
            if (savedInstanceState?.containsKey("query") == false) {
                savedInstanceState.remove("query")
                binding.recyclerView.scrollToPosition(0)
            }
            viewModel.querySearch(it?.toString())
        }

        binding.textInput.setEndIconOnClickListener {
            binding.editText.apply {
                setText("")
                clearFocus()
                binding.recyclerView.scrollToPosition(0)
            }
        }

        binding.filterGamesBtn.apply {
            addBackgroundCircleRipple()
            setOnClickListener {
                FragmentFilterGames().show(supportFragmentManager, FragmentFilterGames.TAG)
            }
        }

        lifecycleScope.launch {
            launch {
                viewModel.isLoading.collectLatest { isLoading ->
                    binding.linearProgressIndicator.apply {
                        isIndeterminate = isLoading
                        isVisible = isLoading
                    }
                }
            }

            launch {
                viewModel.gameList.collectLatest { list ->
                    viewModel.gameAdapter.value?.list = list
                    viewModel.headers(list.filterIsInstance<GameHeaderModel>()
                        .map { gameHeaderModel -> gameHeaderModel.header })
                }
            }

            launch {
                viewModel.updateProgress.collectLatest { updateGamesState ->
                    binding.toolbar.menu?.findItem(R.id.updateGamesMenu)?.isVisible =
                        updateGamesState == null

                    if (updateGamesState == null) {
                        binding.linearProgressIndicator.isVisible = false
                        return@collectLatest
                    }

                    binding.linearProgressIndicator.apply {
                        max = updateGamesState.max
                        setProgressSmoothly(updateGamesState.progress)
                        isVisible = true
                    }
                }
            }
        }

        onBackPressedDispatcher.addCallback(onBackPressedCallback)

        setContentView(binding.root)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        viewModel.getListFromRepository()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.recyclerView.saveState(outState)
    }

    override fun onClickHeader() {
        FragmentSelectHeaderToScroll()
            .show(supportFragmentManager, FragmentSelectHeaderToScroll.TAG)
    }
}