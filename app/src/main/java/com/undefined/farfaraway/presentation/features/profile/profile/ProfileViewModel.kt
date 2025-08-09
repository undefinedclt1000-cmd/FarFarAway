package com.undefined.farfaraway.presentation.features.profile.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


@HiltViewModel
class ProfileViewModel @Inject constructor(

): ViewModel(){



    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems



}