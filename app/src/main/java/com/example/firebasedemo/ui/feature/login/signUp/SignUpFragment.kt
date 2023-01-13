package com.example.firebasedemo.ui.feature.login.signUp

import com.example.firebasedemo.databinding.FragmentSignUpBinding
import com.example.firebasedemo.ui.base.BaseFragment
import org.koin.android.ext.android.inject

class SignUpFragment :
    BaseFragment<FragmentSignUpBinding, SignUpViewModel>(FragmentSignUpBinding::inflate) {

    override val viewModel: SignUpViewModel by inject()

    override fun initView() {
        super.initView()

        binding?.btnSignUp?.setOnClickListener {
            viewModel.createUser(
                binding?.etEmail?.text.toString(),
                binding?.etPassword?.text.toString()
            )
        }

        binding?.topAppBar?.toolbar?.setNavigationOnClickListener {
            closeFragment()
        }

        viewModel.isLogin.observe(requireActivity()){
            if (it){
                viewModel.firebaseUser.value?.let { user ->
                    startMainActivity(user)
                }
            }
        }
    }

}