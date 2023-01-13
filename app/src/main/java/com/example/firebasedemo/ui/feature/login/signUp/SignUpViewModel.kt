package com.example.firebasedemo.ui.feature.login.signUp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.firebasedemo.data.LoginRepository
import com.example.firebasedemo.data.Result
import com.example.firebasedemo.ui.base.BaseViewModel
import com.example.firebasedemo.utils.firebase.FirebaseManager
import com.google.firebase.auth.FirebaseUser

class SignUpViewModel(private val loginRepository: LoginRepository) : BaseViewModel() {

    val firebaseUser = MutableLiveData<FirebaseUser?>()
    val isLogin = MutableLiveData(false)

    fun createUser(user: String, pass: String) {
        launchCoroutine {
            FirebaseManager.createUserWithPasswordAuthentication(user, pass) {
                when (it) {
                    is Result.Success -> {
                        if (it.data is FirebaseUser) {
                            Log.e(TAG, "3 success !.$it", )
                            firebaseUser.value = it.data
                            isLogin.value = true
                        } else {
                            firebaseUser.value = null
                            isLogin.value = false
                        }
                    }
                    is Result.Error -> {
                        Log.e(TAG, "createUser: ${it.exception}", )
                        isLogin.value = false
                    }
                }
            }
        }
    }



    companion object {
        const val TAG = "SignUpViewModel"
    }
}