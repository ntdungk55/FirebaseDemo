package com.example.firebasedemo.utils.firebase

import android.os.Bundle
import android.util.Log
import com.example.firebasedemo.data.Result
import com.example.firebasedemo.data.model.User
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

fun String.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

object FirebaseManager {

    const val TAG: String = "FirebaseManager"
    val analytics: FirebaseAnalytics = Firebase.analytics
    private val database = Firebase.database.reference
    private val readDatabaseListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val user = snapshot.value
            Log.i(TAG, "$user")
        }

        override fun onCancelled(error: DatabaseError) {
            analytics.logEvent(TAG, Bundle().apply { putString("error", error.message) })
        }

    }
    val auth: FirebaseAuth = Firebase.auth

    init {
        database.addValueEventListener(readDatabaseListener)
    }

    fun createUser(user: String, password: String) {
        getUser(user) { result ->
            when (result) {
                is Result.Success -> Log.e(TAG, "User Existed")
                is Result.Error -> {
                    val newUser = User(user, password.sha256(), UUID.randomUUID().toString())
                    database.child("users").child(newUser.email).setValue(newUser)
                }
            }
        }
    }

    fun getUser(email: String, password: String) {
        database.child("users").child(email).get().addOnSuccessListener {
            Log.i(TAG, "Got value ${it.value}")
        }.addOnFailureListener {
            Log.e(TAG, "Error getting data", it)
        }
    }

    fun getUser(user: String, result: (Result<Boolean>) -> Unit) {
        database.child("users").child(user).get().addOnSuccessListener { it ->
            it.value?.let { value ->
                Log.i(TAG, "Got value ${value.toString()}")
                result(Result.Success(true))
            } ?: result(Result.Error(NullPointerException()))
        }.addOnFailureListener {
            Log.e(TAG, "Error getting data", it)
            result(Result.Error(NullPointerException()))
        }
    }

    fun createUserWithPasswordAuthentication(
        email: String,
        password: String,
        result: (Result<Any>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.user?.let { firebaseUser ->
                        result(Result.Success(firebaseUser))
                    }
                } else {
                    result(Result.Error(task.exception))
                }
            }

    }

    fun signInWithPasswordAuthentication(
        email: String,
        password: String,
        result: (Result<Any>) -> Unit
    ) {
        if (email.isEmpty() || password.isEmpty()) {
            result(Result.Error(Exception("Email or Password is emptied")))
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.user?.let { user ->
                        result(Result.Success(user))
                    } ?: kotlin.run {
                        result(Result.Error())
                    }
                } else {
                    result(Result.Error(task.exception))
                }
            }
    }

//    fun checkLoginStatusPasswordAuthentication():Boolean{
//        return auth.currentUser.i
//    }

}




