package com.jeffreyliu.duckit.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.jeffreyliu.duckit.R
import com.jeffreyliu.duckit.adapter.ItemAdapter
import com.jeffreyliu.duckit.databinding.MainFragmentBinding
import com.jeffreyliu.duckit.model.DuckPost
import com.jeffreyliu.duckit.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private var navController: NavController? = null

    private var _binding: MainFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    private val adapter = ItemAdapter(object : ItemAdapter.ItemClickListener {
        override fun onItemClick(post: DuckPost) {

        }

        override fun onItemLongClick(post: DuckPost) {
        }
    })


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setupViewModel()

        binding.recyclerView.adapter = adapter
        binding.swipeContainer.setOnRefreshListener {
            viewModel.getPosts()
        }

        viewModel.getPosts()
    }

    private fun navigateToLoginFragment() {
        if (navController?.currentDestination?.id == R.id.mainFragment) {
            val action = MainFragmentDirections.actionMainFragmentToLoginFragment()
            navController?.navigate(action)
        }
    }

    private fun navigateToAddPostFragment() {
        if (navController?.currentDestination?.id == R.id.mainFragment) {
            val action = MainFragmentDirections.actionMainFragmentToAddPostFragment()
            navController?.navigate(action)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.login_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.login_menu_item) {
            navigateToLoginFragment()
            return true
        }
        return super.onOptionsItemSelected(item)

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
                        is MainViewModel.SetUiState.DoNothing -> {
                            binding.swipeContainer.isRefreshing = false
                        }
                        is MainViewModel.SetUiState.Loading -> {
                            binding.swipeContainer.isRefreshing = true
                        }
                        is MainViewModel.SetUiState.Success -> {
                            binding.swipeContainer.isRefreshing = false
                            adapter.updateList(uiState.posts)
                        }
                        is MainViewModel.SetUiState.Error -> {
                            binding.swipeContainer.isRefreshing = false
                            Snackbar.make(
                                binding.swipeContainer,
                                uiState.errorMsg,
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }
}