package com.undefined.farfaraway.presentation.features.finance.finance

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


@HiltViewModel
class FinanceViewModel @Inject constructor(

): ViewModel(){



    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems



}