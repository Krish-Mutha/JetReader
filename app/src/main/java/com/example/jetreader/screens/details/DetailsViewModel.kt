package com.example.jetreader.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetreader.data.Resource
import com.example.jetreader.model.Item
import com.example.jetreader.repository.BookRepositoryV2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val repo : BookRepositoryV2) : ViewModel() {
    suspend fun getBookInfo(bookId: String) : Resource<Item>{
        return repo.getBookInfo(bookId)
    }
}