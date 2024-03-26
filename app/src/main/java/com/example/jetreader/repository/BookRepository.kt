package com.example.jetreader.repository

import com.example.jetreader.data.DataOrException
import com.example.jetreader.model.Item
import com.example.jetreader.network.BooksApi
import javax.inject.Inject

class BookRepository @Inject constructor(private val api: BooksApi) {
    private val dataOrException = DataOrException<List<Item>,Boolean,Exception>()
    private val bookInfoData = DataOrException<Item,Boolean,Exception>()
    suspend fun getBooks(searchQuery: String) : DataOrException<List<Item>,Boolean,Exception>{
        try {
            dataOrException.loading = true
            dataOrException.data = api.getAllBooks(searchQuery).items
            if(dataOrException.data!!.isNotEmpty()){
                dataOrException.loading = false
            }
        }catch (e: Exception){
            dataOrException.e  =  e
            dataOrException.loading = false
        }
       return dataOrException
    }

    suspend fun getBookInfo(bookId: String): DataOrException<Item,Boolean,Exception>{
        try {
            bookInfoData.loading = true
            bookInfoData.data = api.getBookInfo(bookId)
            if(bookInfoData.data.toString().isNotEmpty()){
                bookInfoData.loading = false
            }
        }catch (e: Exception){
            bookInfoData.e  =  e
            bookInfoData.loading = false
        }
        return bookInfoData
    }
}