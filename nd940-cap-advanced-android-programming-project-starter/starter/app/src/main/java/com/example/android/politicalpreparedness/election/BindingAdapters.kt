package com.example.android.politicalpreparedness.election

import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Election

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, elections: List<Election>?) {
    elections?.let {
        if (elections.isNotEmpty()) {
            (recyclerView.adapter as ElectionListAdapter).submitList(elections)
        }
    }
}

@BindingAdapter("loading")
fun bindLoading(progressBar: ProgressBar, status: ApiStatus?) {
    when (status) {
        ApiStatus.LOADING -> {
            progressBar.visibility = View.VISIBLE
        }
        ApiStatus.ERROR -> {
            progressBar.visibility = View.GONE
        }
        ApiStatus.DONE -> {
            progressBar.visibility = View.GONE
        }
    }
}

@BindingAdapter("voting_location")
fun bindVotingLocation(textView: TextView, deeplink: String?) {
    textView.setTextColor(
        if (!deeplink.isNullOrEmpty()) textView.context.getColor(R.color.blue)
        else textView.context.getColor(R.color.gray50)
    )
}

@BindingAdapter("ballot_info")
fun bindBallotInfo(textView: TextView, deeplink: String?) {
    textView.setTextColor(
        if (!deeplink.isNullOrEmpty()) textView.context.getColor(R.color.blue)
        else textView.context.getColor(R.color.gray50)
    )
}

@BindingAdapter("correspondence_address")
fun bindCorrespondenceAddress(group: Group, address: String?) {
    group.visibility = if (!address.isNullOrEmpty()) View.VISIBLE else View.GONE
}

@BindingAdapter("following_button")
fun bindFollowingButton(button: Button, isFollowing: Boolean) {
    button.text = if (isFollowing) button.context.getString(R.string.unfollow_election)
    else button.context.getString(R.string.follow_election)
}

