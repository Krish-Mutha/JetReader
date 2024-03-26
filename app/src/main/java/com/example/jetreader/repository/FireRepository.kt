package com.example.jetreader.repository

import com.example.jetreader.data.DataOrException
import com.example.jetreader.model.MBook
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FireRepository @Inject constructor(private val queryBook: Query ) {
    suspend fun getAllBooksFromDatabase() : DataOrException<List<MBook>,Boolean,Exception>{
        val dataOrException = DataOrException<List<MBook>,Boolean,Exception>()

        try {
            dataOrException.loading = true
            dataOrException.data = queryBook.get().await().documents.map {
                it.toObject(MBook::class.java)!!
            }

        }catch (ex:  FirebaseFirestoreException){
            dataOrException.e = ex
        }finally {
            dataOrException.loading = false
        }

        return dataOrException
    }

}