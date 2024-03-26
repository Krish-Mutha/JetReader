package com.example.jetreader.screens.search

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetreader.data.DataOrException
import com.example.jetreader.model.Item
import com.example.jetreader.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(private val repo: BookRepository) : ViewModel() {
    val listOfBooks: MutableState<DataOrException<List<Item>,Boolean,Exception>> =
        mutableStateOf(DataOrException(null,true,Exception("")))

    init{
        searchBooks("android")
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            if(query.isEmpty()){
                return@launch
            }
            listOfBooks.value.loading = true
            listOfBooks.value = repo.getBooks(query)
            Log.d("DATA", "search books: ${listOfBooks.value.data.toString()}")
            if(listOfBooks.value.data.toString().isNotEmpty()){
                listOfBooks.value.loading = false
            }
        }
    }
}