package com.example.traveldiary.ui.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveldiary.domain.repository.TravelRepository
import com.example.traveldiary.domain.model.TravelEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Representa los diferentes estados de la pantalla de inicio.
 */
sealed interface HomeUiState {
    object Cargando : HomeUiState
    data class Exito(
        val entradas: List<TravelEntry>,
        val filtroSeleccionado: String = "All",
        val pestanaSeleccionada: Int = 0
    ) : HomeUiState
    data class Error(val mensaje: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: TravelRepository) : ViewModel() {
    
    private val _filtroSeleccionado = mutableStateOf("All")
    private val _pestanaSeleccionada = mutableIntStateOf(0)

    // Combinamos el flujo de datos de la DB con los estados de filtro y pestaña
    val uiState: StateFlow<HomeUiState> = combine(
        repository.getAllEntriesStream(),
        // Convertimos los State de Compose a Flow para poder combinarlos
        snapshotFlow { _filtroSeleccionado.value },
        snapshotFlow { _pestanaSeleccionada.value }
    ) { entradas, filtro, pestana ->
        HomeUiState.Exito(
            entradas = if (filtro == "All") entradas else entradas.filter { it.tag == filtro },
            filtroSeleccionado = filtro,
            pestanaSeleccionada = pestana
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Cargando
    )

    fun onChipSelected(chip: String) {
        _filtroSeleccionado.value = chip
    }

    fun onTabSelected(index: Int) {
        _pestanaSeleccionada.value = index
    }
    
    fun insertSampleEntry(entry: TravelEntry) {
        viewModelScope.launch {
            repository.insertEntry(entry)
        }
    }
}
