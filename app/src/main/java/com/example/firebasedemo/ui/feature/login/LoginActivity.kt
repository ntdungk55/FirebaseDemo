package com.example.firebasedemo.ui.feature.login

import androidx.fragment.app.Fragment
import com.example.firebasedemo.R
import com.example.firebasedemo.databinding.ActivityLoginBinding
import com.example.firebasedemo.ui.base.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    private val auth: FirebaseAuth  = Firebase.auth

    override fun initView() {}

    override fun getIdContainer() = R.id.fm_container

    override fun initFragment(): Fragment? {
        return LoginFragment()
    }

}