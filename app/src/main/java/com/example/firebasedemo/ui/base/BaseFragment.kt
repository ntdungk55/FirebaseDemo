package com.example.firebasedemo.ui.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.firebasedemo.ui.feature.main.MainActivity
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseUser

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewDataBinding, VM : ViewModel>(val inflate: Inflate<VB>) :
    Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding

    abstract val viewModel: VM

    open fun initData(){}
    open fun initView(){}
    open fun initViewModel(){}
    open fun observable(){}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        observable()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun <F : BaseFragment<*,*>> openFragment( fragment : F ){
        parentActivity<BaseActivity<*>> {
            it.openFragment(fragment,BaseActivity.LayoutType.REPLACE,true)
        }
    }

    fun startMainActivity(signInCredential: SignInCredential) {
        val myIntent = Intent(requireActivity(), MainActivity::class.java)
        myIntent.putExtra("credential", signInCredential)
        requireActivity().startActivity(myIntent)
        requireActivity().finish()
    }

    fun startMainActivity(firebaseUser: FirebaseUser) {
        val myIntent = Intent(requireActivity(), MainActivity::class.java)
        myIntent.putExtra("credential", firebaseUser)
        requireActivity().startActivity(myIntent)
        requireActivity().finish()
    }

    fun closeFragment(){
        activity?.supportFragmentManager?.popBackStack()
    }

    protected inline fun <reified A : Activity> parentActivity(callback: (A) -> Unit) =
        (activity as? A)?.let(callback)

    protected inline fun <reified A : Fragment> parentFragment(callback: (A) -> Unit) =
        (parentFragment as? A)?.let(callback)


}