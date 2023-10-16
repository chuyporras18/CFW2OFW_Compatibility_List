package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.fullscreen_cover.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.fragment.app.DialogFragment
import com.lightappsdev.cfw2ofwcompatibilitylist.databinding.GameImageModelBinding
import com.lightappsdev.cfw2ofwcompatibilitylist.load
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentFullscreenCover : DialogFragment() {

    companion object {
        const val TAG: String = "FragmentFullscreenCover"
    }

    private var _binding: GameImageModelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = GameImageModelBinding.inflate(inflater, container, false)

        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.load(arguments?.getString("image"))
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
    }
}