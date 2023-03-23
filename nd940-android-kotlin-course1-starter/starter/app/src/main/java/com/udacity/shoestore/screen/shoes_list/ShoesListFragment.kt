package com.udacity.shoestore.screen.shoes_list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.udacity.shoestore.R
import com.udacity.shoestore.databinding.FragmentShoesListBinding
import com.udacity.shoestore.databinding.ItemViewBinding
import com.udacity.shoestore.models.Shoe
import com.udacity.shoestore.models.ShoesViewModel
import timber.log.Timber

class ShoesListFragment : Fragment() {

    private lateinit var binding: FragmentShoesListBinding

    private val viewModel: ShoesViewModel by activityViewModels()
    private var shoes: Shoe? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shoes_list, container, false)

        viewModel.shoesList.observe(viewLifecycleOwner) { shoesList ->
            binding.shoesListContainer.apply {
                removeAllViews()
                for (shoes in shoesList) {
                    val itemViewBinding = DataBindingUtil.inflate<ItemViewBinding>(
                        inflater,
                        R.layout.item_view,
                        this,
                        false
                    )
                    itemViewBinding.viewModel = viewModel
                    addView(itemViewBinding.root)
                }
            }
        }

        binding.addBtn.setOnClickListener {
            findNavController().navigate(
                ShoesListFragmentDirections.actionShoesListFragmentToShoesDetailFragment()
            )
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val args = ShoesListFragmentArgs.fromBundle(requireArguments())
        shoes = args.shoes
        shoes?.let {
            Timber.d("Shoes from args $shoes")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.shoes_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

}