package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.select_header_to_scroll.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.ListPopupWindow
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.MainActivity
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.select_header_to_scroll.adapters.SelectHeaderToScrollAdapter
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.select_header_to_scroll.listeners.SelectHeaderToScrollAdapterListener
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.select_header_to_scroll.viewmodel.FragmentSelectHeaderToScrollViewModel
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.viewmodel.MainActivityViewModel
import com.lightappsdev.cfw2ofwcompatibilitylist.databinding.FragmentSelectHeaderToScrollBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentSelectHeaderToScroll : DialogFragment(), SelectHeaderToScrollAdapterListener {

    companion object {
        const val TAG: String = "FragmentSelectHeaderToScroll"
    }

    private var _binding: FragmentSelectHeaderToScrollBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FragmentSelectHeaderToScrollViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSelectHeaderToScrollBinding.inflate(inflater, container, false)

        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listener = this
        binding.recyclerView.apply {
            adapter = viewModel.selectHeaderToScrollAdapter.value?.also { adapter ->
                adapter.listener = listener
            } ?: SelectHeaderToScrollAdapter(listener).also { adapter ->
                viewModel.selectHeaderToScrollAdapter(adapter)
            }
        }

        val viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        this.viewModel.fillAdapter(viewModel.headers.value.orEmpty())
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ListPopupWindow.MATCH_PARENT, ListPopupWindow.MATCH_PARENT)
    }

    override fun onClick(s: String) {
        dialog?.dismiss()
        val viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        viewModel.headerDialogResponse(s, requireActivity() as MainActivity)
    }
}