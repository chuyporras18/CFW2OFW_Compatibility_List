package com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.select_header_to_scroll.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lightappsdev.cfw2ofwcompatibilitylist.activity.main.view.fragments.select_header_to_scroll.adapters.SelectHeaderToScrollAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FragmentSelectHeaderToScrollViewModel @Inject constructor() : ViewModel() {

    private val _selectHeaderToScrollAdapter: MutableLiveData<SelectHeaderToScrollAdapter> =
        MutableLiveData()
    val selectHeaderToScrollAdapter: LiveData<SelectHeaderToScrollAdapter> =
        _selectHeaderToScrollAdapter

    fun fillAdapter(list: List<String>) {
        _selectHeaderToScrollAdapter.value?.list = list.toList()
    }

    fun selectHeaderToScrollAdapter(selectHeaderToScrollAdapter: SelectHeaderToScrollAdapter) {
        _selectHeaderToScrollAdapter.value = selectHeaderToScrollAdapter
    }
}