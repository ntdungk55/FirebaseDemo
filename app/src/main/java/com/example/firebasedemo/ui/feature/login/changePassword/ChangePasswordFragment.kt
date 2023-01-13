package com.example.firebasedemo.ui.feature.login.changePassword

import com.example.firebasedemo.databinding.FragmentChangePasswordBinding
import com.example.firebasedemo.ui.base.BaseFragment
import org.koin.android.ext.android.inject

class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding,ChangePasswordViewModel>(FragmentChangePasswordBinding::inflate) {

    override val viewModel: ChangePasswordViewModel by inject()

}
