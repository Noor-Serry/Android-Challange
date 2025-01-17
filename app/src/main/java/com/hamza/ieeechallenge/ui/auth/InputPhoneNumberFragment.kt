package com.hamza.ieeechallenge.ui.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.hamza.ieeechallenge.R
import com.hamza.ieeechallenge.databinding.FragmentInputPhoneNumberBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InputPhoneNumberFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var binding: FragmentInputPhoneNumberBinding

    @SuppressLint("StringFormatInvalid")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputPhoneNumberBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.etPhoneNumber.requestFocus()

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        binding.etCountryName.setOnClickListener {
            findNavController().navigate(InputPhoneNumberFragmentDirections.actionInputPhoneNumberFragmentToSelectCountryFragment())
        }

        binding.btNextToVerify.setOnClickListener {
            viewModel.checkPhoneNumber()
            if (viewModel.numberIsCorrect.value != true) {
                AlertDialog.Builder(requireContext())
                    .setMessage(getString(R.string.phone_number_too_short, viewModel.countryName.value))
                    .setPositiveButton(
                        "OK"
                    ) { dialogInterface, _ -> dialogInterface.dismiss() }
                    .show()
            } else {
                customProgressDialog()
            }
        }

        return binding.root
    }

    @SuppressLint("StringFormatInvalid")
    private fun showConfirmationDialog() {
        val builder =
            AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.phon_number_verifying, viewModel.number.value))
            .setPositiveButton("OK") { _, _ ->
                findNavController().navigate(InputPhoneNumberFragmentDirections.actionInputPhoneNumberFragmentToPhoneVerifyFragment(viewModel.number.value!!))
            }
            .setNegativeButton("Edit") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
    }
    private fun customProgressDialog() {
        val layoutPadding = 50
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.setPadding(layoutPadding, layoutPadding, layoutPadding, layoutPadding)
        linearLayout.gravity = Gravity.START
        var layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER
        linearLayout.layoutParams = layoutParams

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, 24, 0)
        progressBar.layoutParams = layoutParams

        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = getString(R.string.connecting_message)
        tvText.layoutParams = layoutParams

        linearLayout.addView(progressBar)
        linearLayout.addView(tvText)

        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setView(linearLayout)

        val dialog = builder.create()
        val window = dialog.window
        if (window != null) {
            val params = WindowManager.LayoutParams()
            params.copyFrom(dialog.window?.attributes)
            params.width = LinearLayout.LayoutParams.WRAP_CONTENT
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = params
        }

        GlobalScope.launch(Dispatchers.Main) {
            dialog.show()
            delay(1500)
            dialog.dismiss()
            showConfirmationDialog()
        }

    }
}