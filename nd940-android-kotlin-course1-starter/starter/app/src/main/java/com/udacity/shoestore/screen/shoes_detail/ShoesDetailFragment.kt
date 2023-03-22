package com.udacity.shoestore.screen.shoes_detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.udacity.shoestore.R
import com.udacity.shoestore.databinding.FragmentShoesDetailBinding
import com.udacity.shoestore.models.ShoesViewModel

class ShoesDetailFragment : Fragment() {

    private lateinit var binding: FragmentShoesDetailBinding
    private val viewModel: ShoesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.resetValue()
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_shoes_detail, container, false)
        binding.cancelBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.saveBtn.setOnClickListener {
            if (
                binding.promptShoesName.text.toString().isEmpty()
                && binding.promptShoesSize.text.toString().isEmpty()
                && binding.promptCompany.text.toString().isEmpty()
                && binding.promptDescription.text.toString().isEmpty()
            ) {
                Toast.makeText(
                    requireContext(),
                    "Please input all required info",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.addShoes()
                findNavController().navigate(
                    ShoesDetailFragmentDirections
                        .actionShoesDetailFragmentToShoesListFragment()
                )
            }
        }

        binding.viewModel = viewModel
        return binding.root
    }

}