package com.example.jetreader.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetreader.data.Resource
import com.example.jetreader.model.Item
import com.example.jetreader.repository.BookRepositoryV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModelV2 @Inject constructor(private val repo: BookRepositoryV2) : ViewModel() {
    var list: List<Item> by mutableStateOf(listOf())
    var isLoading: Boolean by mutableStateOf(true)

    init{
        loadBooks()
    }

    private fun loadBooks() {
       searchBooks("android")
    }

     fun searchBooks(query: String) {
        viewModelScope.launch {
            isLoading = true
            if(query.isEmpty()) return@launch

            try {
                when(val response = repo.getBooks(query)){
                    is Resource.Success -> {
                        list = response.data!!
                    }
                    is Resource.Error -> {
                        Log.d("Network","Failed getting books")
                    }else -> {

                    }
                }

            }catch (e: Exception){
                Log.d("Network",e.message.toString())
            }finally {
                isLoading = false
            }
        }
    }
}