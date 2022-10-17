package com.example.viewmodel_observables.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.viewmodel_observables.HomeViewModel
import com.example.viewmodel_observables.R
import com.example.viewmodel_observables.SessionViewModel
import com.example.viewmodel_observables.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest


class HomeFragment : Fragment() {
    private val sessionFragmentViewModel: SessionViewModel by activityViewModels()
    private val homeFragmentViewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservables()
        setListeners()
    }

    private fun setObservables() {
        lifecycleScope.launchWhenStarted {
            sessionFragmentViewModel.validAuthToken.collectLatest { isValid ->
                if (!isValid) {
                    Toast.makeText(requireContext(), "Sesion cerrada", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            homeFragmentViewModel.homeState.collectLatest { state ->
                homeStateFun(state)
            }
        }
    }

    private fun homeStateFun(state: HomeViewModel.HomeViewState) {
        when(state){
            is HomeViewModel.HomeViewState.Loading -> {
                setStateLoading(true)
                binding.buttonDefault.isEnabled = state.currentState == HomeViewModel.HomeViewState.Default
                binding.buttonSuccess.isEnabled = state.currentState == HomeViewModel.HomeViewState.Success
                binding.buttonDefault.isEnabled = state.currentState == HomeViewModel.HomeViewState.Failure
                binding.buttonEmpty.isEnabled = state.currentState == HomeViewModel.HomeViewState.Empty
            }

            HomeViewModel.HomeViewState.Default -> {
                setStateLoading(false)
                changeButtonState()

                binding.imageHome.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_android)
                )
                binding.textHomeFragmentDescription.text =
                    getString(R.string.default_string_for_home_fragment)
            }
            HomeViewModel.HomeViewState.Empty -> {
                setStateLoading(false)
                changeButtonState()

                binding.imageHome.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_empty)
                )
                binding.textHomeFragmentDescription.text =
                    getString(R.string.empty_string_for_home_fragment)
            }
            HomeViewModel.HomeViewState.Failure -> {
                setStateLoading(false)
                changeButtonState()

                binding.imageHome.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_failure)
                )
                binding.textHomeFragmentDescription.text =
                    getString(R.string.failure_string_for_home_fragment)
            }
            HomeViewModel.HomeViewState.Success -> {
                setStateLoading(false)
                changeButtonState()

                binding.imageHome.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_success)
                )
                binding.textHomeFragmentDescription.text =
                    getString(R.string.success_string_for_home_fragment)
            }
        }
    }

    private fun changeButtonState() {
        binding.buttonDefault.isEnabled = true
        binding.buttonEmpty.isEnabled = true
        binding.buttonFailure.isEnabled = true
        binding.buttonSuccess.isEnabled = true
    }

    private fun setStateLoading(currentState: Boolean) {
        binding.progressBarHome.visibleIf(currentState)
        binding.imageHome.visibleIf(!currentState)
        binding.textHomeFragmentDescription.visibleIf(!currentState)
    }

    private fun setListeners() {
        binding.apply {
            buttonDefault.setOnClickListener {
                homeFragmentViewModel.changeState(HomeViewModel.HomeViewState.Default)
            }
            buttonEmpty.setOnClickListener {
                homeFragmentViewModel.changeState(HomeViewModel.HomeViewState.Empty)
            }
            buttonFailure.setOnClickListener {
                homeFragmentViewModel.changeState(HomeViewModel.HomeViewState.Failure)
            }
            buttonSuccess.setOnClickListener {
                homeFragmentViewModel.changeState(HomeViewModel.HomeViewState.Success)
            }
            buttonKeepSessionActive.setOnClickListener {
                sessionFragmentViewModel.keepSessionAct()
            }
            buttonLogOut.setOnClickListener {
                sessionFragmentViewModel.logOut()
            }
        }
    }
}