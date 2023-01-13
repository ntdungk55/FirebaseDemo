package com.example.firebasedemo.ui.feature.login.verify

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.firebasedemo.databinding.FragmentVerifyBinding
import com.example.firebasedemo.ui.base.BaseFragment
import com.example.firebasedemo.ui.feature.login.changePassword.ChangePasswordFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.messaging.ktx.remoteMessage
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


class FragmentVerify :
    BaseFragment<FragmentVerifyBinding, VerifyViewModel>(FragmentVerifyBinding::inflate) {

    override val viewModel: VerifyViewModel by inject()

    val fcm = FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            return@addOnCompleteListener
        }
        val token = task.result
    }

    override fun initView() {
        super.initView()

        binding?.etDigit1?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    binding?.etDigit2?.requestFocus()
                }
            }
        })

        binding?.etDigit2?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    binding?.etDigit3?.requestFocus()
                } else {
                    binding?.etDigit1?.requestFocus()
                }
            }
        })

        binding?.etDigit3?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 1) {
                    binding?.etDigit4?.requestFocus()
                } else {
                    binding?.etDigit2?.requestFocus()
                }
            }
        })

        binding?.etDigit4?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    binding?.etDigit3?.requestFocus()
                } else {
                    binding?.btnSend?.callOnClick()
                }
            }
        })

        binding?.btnSend?.setOnClickListener {
            Snackbar.make(requireContext(), requireView(), "Send Success!", Snackbar.LENGTH_LONG)
                .show()
            testPhoneVerify()

        }

        binding?.topAppBar?.toolbar?.setNavigationOnClickListener {
            closeFragment()
        }

    }

    private fun testPhoneVerify() {
        val phoneNum = "+84349625600"
        val testVerificationCode = "123456"

        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNum)
            .setTimeout(30L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.i(TAG, "onCodeSent: ")
                }

                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    Log.i(TAG, "onVerificationCompleted: ")
                    phoneAuthCredential.smsCode?.let {
                        Snackbar.make(
                            requireView(),
                            phoneAuthCredential.smsCode.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        openFragment(ChangePasswordFragment())
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.i(TAG, "onVerificationFailed: ")
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    companion object {
        private const val TAG = "FragmentVerify"
    }
}