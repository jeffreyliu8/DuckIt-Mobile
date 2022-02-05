package com.jeffreyliu.duckit.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.jeffreyliu.duckit.R
import com.jeffreyliu.duckit.data.Result
import com.jeffreyliu.duckit.databinding.AddPostFragmentBinding
import com.jeffreyliu.duckit.extension.exhaustive
import com.jeffreyliu.duckit.viewmodel.AddPostViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddPostFragment : Fragment() {
    private var navController: NavController? = null

    private var _binding: AddPostFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: AddPostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddPostFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        viewModel = ViewModelProvider(this)[AddPostViewModel::class.java]
        setupViewModel()

        binding.button.setOnClickListener {
            hideKeyboard()
            val headline = binding.headLineEditText.text.toString()
            val url = binding.urlEditText.text.toString()
            if (headline.isBlank() || url.isBlank()) {
                Snackbar.make(
                    binding.loading,
                    R.string.invalid_new_post,
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            viewModel.addPost(headline, url)
        }
        binding.urlEditText.setText("https://www.desktopbackground.org/p/2011/12/31/321050_donald-duck-cartoon-wallpapers-image-for-android-cartoons-wallpapers_1600x2200_h.jpg")
    }

    private fun setupViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Trigger the flow and start listening for values.
                // Note that this happens when lifecycle is STARTED and stops
                // collecting when the lifecycle is STOPPED
                viewModel.uiState.collectLatest { uiState ->
                    // New value received
                    when (uiState) {
                        is Result.DoNothing -> {
                            binding.loading.visibility = View.GONE
                        }
                        is Result.Loading -> {
                            binding.loading.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.loading.visibility = View.GONE
                            navigateToAddMainFragmentAfterPosted()
                        }
                        is Result.Error -> {
                            binding.loading.visibility = View.GONE
                            Snackbar.make(
                                binding.loading,
                                uiState.errorMsg,
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }.exhaustive
                }
            }
        }
    }

    private fun navigateToAddMainFragmentAfterPosted() {
        if (navController?.currentDestination?.id == R.id.addPostFragment) {
            val action = AddPostFragmentDirections.actionAddPostFragmentToMainFragment()
            navController?.navigate(action)
        }
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}