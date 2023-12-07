package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.filter_games.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.ListPopupWindow.MATCH_PARENT
import androidx.appcompat.widget.ListPopupWindow.WRAP_CONTENT
import androidx.core.view.allViews
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.radiobutton.MaterialRadioButton
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums.GameFiltersType
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.enums.GameWorkTypes
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.filter_games.enums.SortGamesTypes
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.filter_games.viewmodel.FragmentFilterGamesViewModel
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.viewmodel.MainActivityViewModel
import com.lightappsdev.cfw2ofwcompatibilitylist.databinding.FragmentFilterGamesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentFilterGames : DialogFragment() {

    companion object {
        const val TAG: String = "FragmentFilterGames"

        private val ALL_GAMES_CHECKBOX_ID: Int = "all_games_checked_filter".hashCode()
    }

    private var _binding: FragmentFilterGamesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FragmentFilterGamesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFilterGamesBinding.inflate(inflater, container, false)

        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelBtn.setOnClickListener { dialog?.dismiss() }

        val gameIds = viewModel.gameIdsProvider.generateGameIds()

        binding.acceptBtn.setOnClickListener {
            val viewModel =
                ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
            val sortGamesTypes = this.viewModel.sortGamesTypes.value
            val booleanArray = this.viewModel.checkedFilter.value
            val filterBooleanArray = this.viewModel.gameCheckedFilter.value
            val allGamesCheckedFilter = this.viewModel.allGamesCheckedFilter.value
            val gameIdsCheckedFilter = this.viewModel.gameIdsCheckedFilter.value?.toList().orEmpty()
                .mapIndexedNotNull { index, b ->
                    if (b) {
                        gameIds[index]
                    } else {
                        null
                    }
                }.toSet()
            viewModel.filterDialogResponse(
                sortGamesTypes = sortGamesTypes,
                booleanArray = booleanArray,
                filterBooleanArray = filterBooleanArray,
                allGamesCheckedFilter = allGamesCheckedFilter,
                gameIdsSet = gameIdsCheckedFilter
            )
            dialog?.dismiss()
        }

        val sortGamesTypes = viewModel.getSortGameType()
        SortGamesTypes.values().forEachIndexed { index, gamesTypes ->
            val radioButton = MaterialRadioButton(binding.root.context).apply {
                id = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    View.generateViewId()
                } else {
                    "${gamesTypes.name}_$index".hashCode()
                }
                text = gamesTypes.title
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        viewModel.sortGameTypes(gamesTypes)
                    }
                }
                isChecked = gamesTypes == sortGamesTypes
            }
            binding.radioGroup.addView(radioButton)
        }

        addAllGamesCheckBox()

        val gameIdsBooleanArray = viewModel.getGameIdsCheckedFilters()
        gameIds.forEachIndexed { index, s ->
            val checkBox = generateCheckBox(
                message = s,
                isChecked = gameIdsBooleanArray[index],
                id = "${s}_$index".hashCode()
            ) { isChecked ->
                gameIdsBooleanArray[index] = isChecked
                viewModel.gameIdsCheckedFilter(gameIdsBooleanArray)
            }

            binding.idsCheckBox.addView(checkBox)
        }

        val gameWorksBooleanArray = viewModel.getGameWorkCheckedFilters()
        GameWorkTypes.values().forEachIndexed { index, gameWorkTypes ->
            val checkBox = generateCheckBox(
                message = gameWorkTypes.filterMessage,
                isChecked = gameWorksBooleanArray[index],
                id = "${gameWorkTypes.name}_$index".hashCode()
            ) { isChecked ->
                gameWorksBooleanArray[index] = isChecked
                viewModel.checkedFilters(gameWorksBooleanArray)
            }
            binding.checkBox.addView(checkBox)
        }

        val gameBooleanArray = viewModel.getGameCheckedFilters()
        GameFiltersType.values().forEachIndexed { index, gameFiltersType ->
            if (gameFiltersType == GameFiltersType.NOT_PLATINUM) {
                return@forEachIndexed
            }

            val checkBox = generateCheckBox(
                message = gameFiltersType.filterMessage,
                isChecked = gameBooleanArray[index],
                id = "${gameFiltersType.name}_$index".hashCode()
            ) { isChecked ->
                gameBooleanArray[index] = isChecked
                viewModel.gameCheckedFilters(gameBooleanArray)
            }
            binding.checkBox.addView(checkBox)
        }

        toggleCheckboxOnAllGamesChecked(viewModel.getAllGamesFilterChecked())
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(MATCH_PARENT, WRAP_CONTENT)
    }

    private fun addAllGamesCheckBox() {
        val isAllGamesChecked = viewModel.getAllGamesFilterChecked()
        viewModel.allGamesCheckedFilter(isAllGamesChecked)
        val checkBox =
            generateCheckBox("Todos los Juegos y Regiones", isAllGamesChecked, ALL_GAMES_CHECKBOX_ID) { isChecked ->
                toggleCheckboxOnAllGamesChecked(isChecked)
                viewModel.toggleAllGamesCheckedFilter()
            }
        binding.checkBox.addView(checkBox)
    }

    private fun toggleCheckboxOnAllGamesChecked(isChecked: Boolean) {
        (binding.checkBox.allViews + binding.idsCheckBox.allViews).forEach { view ->
            if (view.id == ALL_GAMES_CHECKBOX_ID || view !is MaterialCheckBox) {
                return@forEach
            }

            view.isEnabled = !isChecked
        }
    }

    private fun generateCheckBox(
        message: String,
        isChecked: Boolean,
        id: Int,
        onCheckedChange: (isChecked: Boolean) -> Unit
    ): CheckBox {
        return MaterialCheckBox(binding.root.context).apply {
            this.id = id
            text = message
            this.isChecked = isChecked
            setOnCheckedChangeListener { _, isChecked -> onCheckedChange(isChecked) }
        }
    }
}