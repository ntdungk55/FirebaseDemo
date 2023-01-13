package com.example.firebasedemo.ui.feature.login

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebasedemo.R
import com.example.firebasedemo.data.LoginRepository
import com.example.firebasedemo.data.Result
import com.example.firebasedemo.data.model.LoggedInUser
import com.example.firebasedemo.ui.base.BaseViewModel
import com.example.firebasedemo.utils.firebase.FirebaseManager
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson

class LoginViewModel(private val loginRepository: LoginRepository) : BaseViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _isLogin = MutableLiveData(false)
    val isLogin = _isLogin

    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser = _firebaseUser

    fun login(username: String, password: String) {

        FirebaseManager.getUser(username) {}

        val result = loginRepository.login(username, password)
        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
            FirebaseManager.analytics.logEvent(
                "UerInfo",
                Bundle().apply {
                    putString(
                        "user", Gson().toJson(LoggedInUser(username, password))
                    )
                })
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun firebasePasswordSignIn(email: String, password: String, result: (Result<*>) -> Unit) {
        launchCoroutine {
            FirebaseManager.signInWithPasswordAuthentication(email, password) {
                result(it)
            }
        }
    }

}