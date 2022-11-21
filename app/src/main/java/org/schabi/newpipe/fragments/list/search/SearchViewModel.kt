package org.schabi.newpipe.fragments.list.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.search.filter.FilterItem
import org.schabi.newpipe.fragments.list.search.filter.SearchFilterLogic
import org.schabi.newpipe.fragments.list.search.filter.SearchFilterLogic.Factory.Variant

class SearchViewModel(
    val serviceId: Int,
    logicVariant: Variant,
    userSelectedContentFilterList: List<Int>,
    userSelectedSortFilterList: List<Int>
) : ViewModel() {

    private val selectedContentFilterMutableLiveData: MutableLiveData<MutableList<FilterItem>> =
        MutableLiveData()
    private var selectedSortFilterLiveData: MutableLiveData<MutableList<FilterItem>> =
        MutableLiveData()
    private var userSelectedSortFilterListMutableLiveData: MutableLiveData<ArrayList<Int>> =
        MutableLiveData()
    private var userSelectedContentFilterListMutableLiveData: MutableLiveData<ArrayList<Int>> =
        MutableLiveData()
    private var doSearchMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val selectedContentFilterItemListLiveData: LiveData<MutableList<FilterItem>>
        get() = selectedContentFilterMutableLiveData
    val selectedSortFilterItemListLiveData: LiveData<MutableList<FilterItem>>
        get() = selectedSortFilterLiveData
    val userSelectedContentFilterListLiveData: LiveData<ArrayList<Int>>
        get() = userSelectedContentFilterListMutableLiveData
    val userSelectedSortFilterListLiveData: LiveData<ArrayList<Int>>
        get() = userSelectedSortFilterListMutableLiveData
    val doSearchLiveData: LiveData<Boolean>
        get() = doSearchMutableLiveData

    val searchFilterLogic = SearchFilterLogic.Factory.create(
        logicVariant, NewPipe.getService(serviceId).searchQHFactory, null
    )

    init {
        searchFilterLogic.restorePreviouslySelectedFilters(
            userSelectedContentFilterList,
            userSelectedSortFilterList
        )

        searchFilterLogic.setCallback { userSelectedContentFilter: List<FilterItem?>,
            userSelectedSortFilter: List<FilterItem?> ->
            selectedContentFilterMutableLiveData.value =
                userSelectedContentFilter as MutableList<FilterItem>
            selectedSortFilterLiveData.value =
                userSelectedSortFilter as MutableList<FilterItem>
            userSelectedContentFilterListMutableLiveData.value =
                searchFilterLogic.selectedContentFilters
            userSelectedSortFilterListMutableLiveData.value =
                searchFilterLogic.selectedSortFilters

            doSearchMutableLiveData.value = true
        }
    }

    fun weConsumedDoSearchLiveData() {
        doSearchMutableLiveData.value = false
    }

    companion object {

        fun getFactory(
            serviceId: Int,
            logicVariant: Variant,
            userSelectedContentFilterList: ArrayList<Int>,
            userSelectedSortFilterList: ArrayList<Int>
        ) = viewModelFactory {
            initializer {
                SearchViewModel(
                    serviceId,
                    logicVariant,
                    userSelectedContentFilterList,
                    userSelectedSortFilterList
                )
            }
        }
    }
}
