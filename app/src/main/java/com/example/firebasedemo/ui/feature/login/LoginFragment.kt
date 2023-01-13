package com.example.firebasedemo.ui.feature.login

import android.content.IntentSender
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import com.example.firebasedemo.R
import com.example.firebasedemo.data.Result
import com.example.firebasedemo.databinding.FragmentLoginBinding
import com.example.firebasedemo.ui.base.BaseFragment
import com.example.firebasedemo.ui.base.BaseViewModel
import com.example.firebasedemo.ui.feature.login.signUp.SignUpFragment
import com.example.firebasedemo.ui.feature.login.forgotPassword.ForgotPasswordFragment
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.koin.android.ext.android.inject


class LoginFragment :
    BaseFragment<FragmentLoginBinding, LoginViewModel>(FragmentLoginBinding::inflate) {

    private lateinit var signInClient: SignInClient

    override val viewModel: LoginViewModel by inject()

    private var auth = FirebaseAuth.getInstance()

    private val oneTapClient: SignInClient by lazy {
        Identity.getSignInClient(requireActivity())
    }

    private val registerSignInRequestResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken

                idToken?.let {
                    Snackbar.make(requireView(), idToken, Snackbar.LENGTH_LONG).show()
                    startMainActivity(credential)
                } ?: run {
                    Snackbar.make(requireView(), "No ID token", Snackbar.LENGTH_LONG).show()
                }
            } catch (e: ApiException) {
            }
        }

    private val registerSignUpRequestResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                idToken?.let {
                    Snackbar.make(requireView(), idToken, Snackbar.LENGTH_LONG).show()
                    startMainActivity(credential)
                } ?: run {
                    Snackbar.make(requireView(), "No ID token", Snackbar.LENGTH_LONG).show()
                }
            } catch (e: ApiException) {

            }
        }

    private val signInRequest: BeginSignInRequest by lazy {
        BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    private val signUpRequest: BeginSignInRequest by lazy {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    override fun initView() {
        signInClient = Identity.getSignInClient(requireContext())
        viewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                binding?.btnLogin?.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    binding?.etEmail?.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    binding?.etEmail?.error = getString(it)
                }
            })

        viewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loginResult.error?.let {
                    showLoginFailed(it)
                }
                loginResult.success?.let {
                    updateUiWithUser(it)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                viewModel.loginDataChanged(
                    binding?.etEmail?.text.toString(),
                    binding?.etPassword?.text.toString()
                )
            }
        }
        binding?.etEmail?.addTextChangedListener(afterTextChangedListener)
        binding?.etPassword?.addTextChangedListener(afterTextChangedListener)
        binding?.etPassword?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.login(
                    binding?.etEmail?.text.toString(),
                    binding?.etPassword?.text.toString()
                )
            }
            false
        }

        binding?.tvForgotPassword?.setOnClickListener {
            openFragment(ForgotPasswordFragment())
        }

        binding?.btnLogin?.setOnClickListener {
            firebasePasswordSignIn()
        }

        binding?.btnSignUp?.setOnClickListener {
            openFragment(SignUpFragment())
        }

        binding?.btnGoogleLogin?.setOnClickListener {
            googleSignIn()
        }

        binding?.btnGoogleSignUp?.setOnClickListener {
            googleSignUp()
        }
    }

    private fun googleSignIn() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(requireActivity()) {
                try {
                    registerSignInRequestResult.launch(
                        IntentSenderRequest.Builder(it.pendingIntent.intentSender).build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    binding?.root?.let {
                        Snackbar.make(
                            it,
                            "Couldn't start One Tap UI: ${e.localizedMessage}",
                            Snackbar.LENGTH_LONG
                        )
                    }
                }
            }
            .addOnFailureListener(requireActivity()) { e ->
                binding?.root?.let {
                    Snackbar.make(it, e.localizedMessage, Snackbar.LENGTH_LONG).show()
                }
            }
    }

    private fun googleSignUp() {
        oneTapClient.beginSignIn(signUpRequest)
            .addOnSuccessListener(requireActivity()) { result ->
                try {
                    registerSignUpRequestResult.launch(
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    binding?.root?.let {
                        Snackbar.make(it, "Can not send intent ", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            .addOnFailureListener(requireActivity()) { e ->
                binding?.root?.let {
                    Snackbar.make(it, e.localizedMessage, Snackbar.LENGTH_LONG).show()
                }
            }
    }

    private fun firebasePasswordSignIn() {
        viewModel.firebasePasswordSignIn(
            binding?.etEmail?.text.toString(),
            binding?.etPassword?.text.toString()
        ) {
            when (it) {
                is Result.Success -> {
                    if (it.data is FirebaseUser) {
                        startMainActivity(it.data)
                    }
                }
                is Result.Error -> {
                    view?.let { view ->
                        Snackbar.make(view, it.exception?.message.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + model.displayName
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    companion object {
        val TAG: String? = LoginFragment::class.simpleName
        const val REQ_ONE_TAP_SIGN_IN = 1
        const val REQ_ONE_TAP_SIGN_UP = 2
    }
}