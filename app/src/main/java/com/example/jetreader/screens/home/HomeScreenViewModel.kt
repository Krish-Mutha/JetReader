package com.example.jetreader.screens.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetreader.data.DataOrException
import com.example.jetreader.model.MBook
import com.example.jetreader.repository.FireRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private  val repo : FireRepository) : ViewModel() {
    val data: MutableState<DataOrException<List<MBook>,Boolean,Exception>> =
        mutableStateOf(DataOrException(listOf(),true,Exception("")))

    init {
        getAllBooksFromDb()
    }

    private fun getAllBooksFromDb() {
       viewModelScope.launch {
           data.value.loading = true
           data.value = repo.getAllBooksFromDatabase()
           if(!data.value.data.isNullOrEmpty())  data.value.loading = false
           Log.d("DATA",data.value.data.toString())
       }


    }

}