package com.example.firebasedemo.di

import com.example.firebasedemo.data.LoginDataSource
import com.example.firebasedemo.data.LoginRepository
import com.example.firebasedemo.ui.feature.login.LoginViewModel
import com.example.firebasedemo.ui.feature.login.changePassword.ChangePasswordViewModel
import com.example.firebasedemo.ui.feature.login.forgotPassword.ForgotPasswordViewModel
import com.example.firebasedemo.ui.feature.login.signUp.SignUpViewModel
import com.example.firebasedemo.ui.feature.login.verify.VerifyViewModel
import org.koin.dsl.module

val module = module {
    single { LoginDataSource() }
    single { LoginRepository(get()) }
    single { LoginViewModel(get()) }
    single { SignUpViewModel(get()) }
    single { ForgotPasswordViewModel(get()) }
    single { VerifyViewModel(get()) }
    single { ChangePasswordViewModel(get()) }
}