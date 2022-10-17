package com.example.viewmodel_observables.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.viewmodel_observables.HomeViewModel
import com.example.viewmodel_observables.R
import com.example.viewmodel_observables.SessionViewModel
import com.example.viewmodel_observables.databinding.FragmentHomeBinding
import com.example.viewmodel_observables.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest

fun View.visibleIf(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val sessionViewModel: SessionViewModel by activityViewModels()
    private lateinit var job: Job

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservables()
        setListeners()
    }

    private fun setListeners() {
        binding.buttonLogin.setOnClickListener {
            sessionViewModel.login(
                email = binding.emailInput.editText!!.text.toString(),
                password = binding.passwordInput.editText!!.text.toString()
            )
        }
    }

    private fun setObservables() {
        job = lifecycleScope.launchWhenStarted {
            sessionViewModel.loginStatus.collectLatest { state ->
                loginStateFun(state)
            }
        }
    }

    private fun loginStateFun(state: SessionViewModel.LoginState) {
        when(state){
            SessionViewModel.LoginState.Default -> {
                binding.progressLoginFragment.visibleIf(false)
                binding.buttonLogin.visibleIf(true)
            }
            SessionViewModel.LoginState.Success -> {
                sessionViewModel.generateToken()
                job.cancel()
                requireView().findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                )
            }
            SessionViewModel.LoginState.Loading -> {
                binding.progressLoginFragment.visibleIf(true)
                binding.buttonLogin.visibility = View.INVISIBLE
            }
            is SessionViewModel.LoginState.Error -> {
                binding.progressLoginFragment.visibleIf(false)
                binding.buttonLogin.visibleIf(true)
                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}