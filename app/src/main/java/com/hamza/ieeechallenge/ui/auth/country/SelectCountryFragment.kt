package com.hamza.ieeechallenge.ui.auth.country

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hamza.ieeechallenge.R
import com.hamza.ieeechallenge.databinding.FragmentSelectCountryBinding
import com.hamza.ieeechallenge.ui.auth.AuthViewModel
import com.hamza.ieeechallenge.utils.Resource
import com.hamza.ieeechallenge.utils.formatDialCode

class SelectCountryFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()
    private val countryViewModel: CountryViewModel by activityViewModels()
    private lateinit var binding: FragmentSelectCountryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectCountryBinding.inflate(inflater, container, false)

        val countryAdapter = SelectCountryAdapter(SelectCountryAdapter.CountryListener {
            val formattedDialCode = formatDialCode(it.dial_code)
            authViewModel.countryName.value = it.name
            authViewModel.countryDialCode.value = formattedDialCode
            findNavController().navigate(SelectCountryFragmentDirections.actionSelectCountryFragmentToInputPhoneNumberFragment())
        })

        countryViewModel.countryList.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    response.data?.let { countryList ->
                        countryAdapter.submitList(countryList)
                        binding.rvCountry.adapter = countryAdapter
                    }
                    binding.loading.visibility = View.GONE
                }
                is Resource.Error ->  {
                    binding.loading.visibility = View.GONE
                    response.message?.let { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> binding.loading.visibility = View.VISIBLE
            }
        })

        return binding.root
    }
}