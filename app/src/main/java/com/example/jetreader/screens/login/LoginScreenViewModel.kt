package com.example.jetreader.screens.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetreader.model.MUser
import com.example.jetreader.utils.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginScreenViewModel: ViewModel() {
    //val loadingState = MutableStateFlow(LoadingState.IDLE)
    private val auth: FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun signInFireAuth(email:String,password:String, home: () -> Unit = {})
    = viewModelScope.launch{
        try {
            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{task ->
                    if(task.isSuccessful) {
                        Log.d("FB", "SignIn: ${task.result.toString()}")
                        home()
                    }else{
                        Log.d("FB", "SignIn Error: ${task.result.toString()}")
                    }
                }

        }catch (ex: Exception){
            Log.d("FB", "SignIn Critical Error: ${ex.message}")
        }
    }
    fun createUserFireAuth(email:String,password:String, home: () -> Unit = {}) = viewModelScope.launch{
        if (_loading.value == false){
            _loading.value = true
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{ task ->
                    if(task.isSuccessful) {
                        Log.d("FB", "SignIn: ${task.result.toString()}")
                        val displayName = task.result.user?.email?.split("@")?.get(0)
                        createUserFireStore(displayName!!)
                        home()
                    }else{
                        Log.d("FB", "SignIn Error: ${task.result.toString()}")
                    }
                }
            _loading.value = false
        }
    }

    private fun createUserFireStore(displayName: String){
        val userId= auth.currentUser?.uid
        val user = MUser(userId = userId.toString(), displayName = displayName, avatarUrl = "", quote = "Life is great", profession = "Android Developer",id = null).toMap()
        FirebaseFirestore.getInstance().collection("users").add(user)
    }




}