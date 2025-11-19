package com.childprotectionsystems.trackerpro.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val placeAutocomplete = PlaceAutocomplete.create(locationProvider = null)

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _suggestions = MutableStateFlow<List<PlaceAutocompleteSuggestion>>(emptyList())
    val suggestions: StateFlow<List<PlaceAutocompleteSuggestion>> = _suggestions

    private val _selectedPoint = MutableStateFlow<Point?>(null)
    val selectedPoint: StateFlow<Point?> = _selectedPoint

    fun onQueryChanged(text: String) {
        _query.value = text

        if (text.isBlank()) {
            _suggestions.value = emptyList()
            return
        }

        viewModelScope.launch {
            val response = placeAutocomplete.suggestions(query = text)

            if (response.isValue) {
                _suggestions.value = response.value ?: emptyList()
            } else {
                _suggestions.value = emptyList()
            }
        }
    }

    fun selectSuggestion(suggestion: PlaceAutocompleteSuggestion) {
        viewModelScope.launch {
            val result = placeAutocomplete.select(suggestion)

            result.onValue { value ->
                _selectedPoint.value = value.coordinate
            }

            result.onError {
                _selectedPoint.value = null
            }
        }
    }

    fun clearResults() {
        _suggestions.value = emptyList()
    }
}