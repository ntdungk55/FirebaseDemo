package com.example.firebasedemo.ui.base

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.firebasedemo.R


abstract class BaseActivity<VB : ViewDataBinding>(val bindingFactory: (LayoutInflater) -> VB) :
    AppCompatActivity() {

    private var mProgressDialog: ProgressDialog? = null

    val binding: VB by lazy { bindingFactory(layoutInflater) }

    abstract fun initView()
    abstract fun getIdContainer(): Int
    abstract fun initFragment(): Fragment?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        openFragment(initFragment())
    }

    fun hideLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.cancel()
        }
    }

    fun showLoading() {
        hideLoading()
        mProgressDialog = showLoadingDialog(this)
    }

    fun showLoadingDialog(context: Context?): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.show()
        if (progressDialog.window != null) {
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        progressDialog.setContentView(R.layout.layout_progress_dialog)
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        return progressDialog
    }

    fun openFragment(
        fragment: Fragment?,
        layoutType: LayoutType = LayoutType.ADD,
        addToBackStack: Boolean = false
    ) {
        if (fragment == null) return
        val transaction = supportFragmentManager
            .beginTransaction()
        transaction.setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        if (layoutType == LayoutType.ADD) transaction.add(getIdContainer(), fragment)
        else transaction.replace(getIdContainer(), fragment)

        if (addToBackStack)
            transaction.addToBackStack(fragment.javaClass.simpleName)

        transaction.commit()
    }

    enum class LayoutType {
        ADD, REPLACE
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    fun hideKeyboard() {
        this.currentFocus?.let {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

}