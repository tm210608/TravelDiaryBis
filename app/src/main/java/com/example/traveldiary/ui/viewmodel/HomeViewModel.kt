package com.example.traveldiary.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveldiary.domain.model.HomeFilter
import com.example.traveldiary.domain.model.TravelEntry
import com.example.traveldiary.domain.usecase.AddEntryUseCase
import com.example.traveldiary.domain.usecase.FilterEntriesUseCase
import com.example.traveldiary.domain.usecase.GetEntriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeListState {
    data class Results(val entries: List<TravelEntry>, val featured: List<TravelEntry>) : HomeListState
    data class NoResults(val filter: HomeFilter, val query: String) : HomeListState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getEntriesUseCase: GetEntriesUseCase,
    private val addEntryUseCase: AddEntryUseCase,
    private val filterEntriesUseCase: FilterEntriesUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val selectedFilter: StateFlow<HomeFilter> =
        savedStateHandle.getStateFlow(KEY_SELECTED_FILTER, HomeFilter.TODO)

    private val _selectedTab = mutableIntStateOf(0)
    val selectedTab: State<Int> = _selectedTab

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val listState: StateFlow<HomeListState> = combine(
        getEntriesUseCase(), selectedFilter, _searchQuery
    ) { entries, filter, query ->
        val filtered = filterEntriesUseCase(entries, filter, query)
        if (filtered.isEmpty() && entries.isNotEmpty()) {
            HomeListState.NoResults(filter, query)
        } else {
            HomeListState.Results(filtered, filtered.filter { it.isFavourite }.take(FEATURED_LIMIT))
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeListState.Results(emptyList(), emptyList()),
    )

    fun onFilterSelected(filter: HomeFilter) {
        savedStateHandle[KEY_SELECTED_FILTER] = filter
    }

    fun onShowAll() {
        savedStateHandle[KEY_SELECTED_FILTER] = HomeFilter.TODO
        _searchQuery.value = ""
    }

    fun onTabSelected(index: Int) {
        _selectedTab.value = index
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun insertSampleEntry(entry: TravelEntry) {
        viewModelScope.launch {
            addEntryUseCase(entry)
        }
    }

    private companion object {
        const val KEY_SELECTED_FILTER = "selected_filter"
        const val FEATURED_LIMIT = 3
    }
}
