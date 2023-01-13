package com.example.firebasedemo.ui.feature.login.forgotPassword

import com.example.firebasedemo.databinding.FragmentForgotPasswordBinding
import com.example.firebasedemo.ui.base.BaseFragment
import com.example.firebasedemo.ui.feature.login.verify.FragmentVerify
import org.koin.android.ext.android.inject

class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding,ForgotPasswordViewModel>(FragmentForgotPasswordBinding::inflate) {

    override val viewModel: ForgotPasswordViewModel by inject()

    override fun initView() {
        super.initView()

        binding?.topAppBar?.toolbar?.setNavigationOnClickListener {
            closeFragment()
        }

        binding?.btnSend?.setOnClickListener{
            openFragment(FragmentVerify())
        }

    }

}