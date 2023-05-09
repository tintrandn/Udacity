package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var viewModel: VoterInfoViewModel

    private val args: VoterInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val application = requireNotNull(this.activity).application
        val viewModelFactory = VoterInfoViewModelFactory(application, args.argElection)
        viewModel = ViewModelProvider(this, viewModelFactory)[VoterInfoViewModel::class.java]

        val binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.votingLocationDeeplink.setOnClickListener {
            viewModel.votingLocationUrl.value?.let {
                startWebView(it)
            }
        }

        binding.ballotInfoDeeplink.setOnClickListener {
            viewModel.ballotInfoUrl.value?.let {
                startWebView(it)
            }
        }

        viewModel.error.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        return binding.root
    }

    private fun startWebView(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}