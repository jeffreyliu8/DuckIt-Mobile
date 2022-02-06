package com.jeffreyliu.duckit.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.jeffreyliu.duckit.R
import com.jeffreyliu.duckit.databinding.LoginFragmentBinding
import com.jeffreyliu.duckit.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var navController: NavController? = null

    private var _binding: LoginFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val loginButton = binding.loginButton
        val signUpButton = binding.registerButton
        val loadingProgressBar = binding.loading

        viewModel.loginFormState.observe(viewLifecycleOwner, Observer { loginFormState ->
            if (loginFormState == null) {
                return@Observer
            }
            loginButton.isEnabled = loginFormState.isDataValid
            signUpButton.isEnabled = loginFormState.isDataValid
            loginFormState.usernameError?.let {
                usernameEditText.error = getString(it)
            }
            loginFormState.passwordError?.let {
                passwordEditText.error = getString(it)
            }
        })

        viewModel.loginResult.observe(viewLifecycleOwner, Observer { loginResult ->
            loginResult ?: return@Observer
            loadingProgressBar.visibility = View.GONE
            loginResult.error?.let {
                showLoginFailed(it)
            }
            if (loginResult.success == true) {
                navigateToMainFragmentAfterSuccessfulLogin()
            }
        })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                viewModel.loginDataChanged(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                viewModel.loginOrSignUp(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString(),
                )
            }
            false
        }

        loginButton.setOnClickListener {
            hideKeyboard()
            loadingProgressBar.visibility = View.VISIBLE
            viewModel.loginOrSignUp(
                usernameEditText.text.toString(),
                passwordEditText.text.toString(),
            )
        }

        signUpButton.setOnClickListener {
            hideKeyboard()
            loadingProgressBar.visibility = View.VISIBLE
            viewModel.loginOrSignUp(
                usernameEditText.text.toString(),
                passwordEditText.text.toString(),
                isSignUp = true
            )
        }
    }

    private fun navigateToMainFragmentAfterSuccessfulLogin() {
        if (navController?.currentDestination?.id == R.id.loginFragment) {
            val action = LoginFragmentDirections.actionLoginFragmentToMainFragment()
            navController?.navigate(action)
        }
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Snackbar.make(
            binding.registerButton,
            errorString,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}