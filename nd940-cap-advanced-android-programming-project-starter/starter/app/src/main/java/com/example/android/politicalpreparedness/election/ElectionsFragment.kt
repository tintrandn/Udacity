package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment : Fragment() {

    private lateinit var viewModel: ElectionsViewModel

    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val application = requireNotNull(this.activity).application
        val viewModelFactory = ElectionsViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)[ElectionsViewModel::class.java]

        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                navController.navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it))
                viewModel.displayVoterInfoCompleted()
            }
        })

        val upcomingElectionAdapter = ElectionListAdapter(ElectionListener {
            viewModel.displayVoterInfo(it)
        })

        val savedElectionsAdapter = ElectionListAdapter(ElectionListener {
            viewModel.displayVoterInfo(it)
        })

        binding.upcomingElectionsRecycleView.adapter = upcomingElectionAdapter
        binding.savedElectionsRecycleView.adapter = savedElectionsAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchSavedElections()
    }

}