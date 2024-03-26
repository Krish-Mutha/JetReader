package com.example.jetreader.repository

import com.example.jetreader.data.Resource
import com.example.jetreader.model.Item
import com.example.jetreader.network.BooksApi
import javax.inject.Inject

class BookRepositoryV2 @Inject constructor(private val api: BooksApi) {
    suspend fun getBooks(searchQuery: String) : Resource<List<Item>>{
        return try {
            Resource.Loading(data = true)
            val itemList = api.getAllBooks(searchQuery).items
            Resource.Loading(data = false)
            Resource.Success(data = itemList)
        }catch (ex: Exception){
            Resource.Error(message = ex.message)
        }finally {
            Resource.Loading(data = false)
        }
    }

    suspend fun getBookInfo(bookId: String) : Resource<Item>{
        return try {
            Resource.Loading(data = true)
            val book = api.getBookInfo(bookId)
            Resource.Loading(data = false)
            Resource.Success(data = book)
        }catch (ex: Exception){
            Resource.Error(message = ex.message)
        }finally {
            Resource.Loading(data = false)
        }
    }
}