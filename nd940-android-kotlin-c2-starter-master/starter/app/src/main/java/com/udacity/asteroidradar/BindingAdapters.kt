package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.main.ApiStatus
import com.udacity.asteroidradar.main.AsteroidAdapter

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
        imageView.contentDescription =
            imageView.context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
        imageView.contentDescription =
            imageView.context.getString(R.string.normal_hazardous_asteroid_image)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
        imageView.contentDescription =
            imageView.context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
        imageView.contentDescription =
            imageView.context.getString(R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("pictureOfDay")
fun bindPictureOfDay(imageView: ImageView, pictureOfDay: PictureOfDay?) {
    if (pictureOfDay == null) {
        imageView.setImageResource(R.drawable.ic_connection_error)
        imageView.contentDescription =
            imageView.context.getString(R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet)
    } else {
        if (pictureOfDay.mediaType == "image") {
            Picasso.with(imageView.context)
                .load(pictureOfDay.url)
                .placeholder(R.drawable.placeholder_picture_of_day)
                .error(R.drawable.placeholder_picture_of_day)
                .into(imageView)
            imageView.contentDescription = imageView.context.getString(
                R.string.nasa_picture_of_day_content_description_format,
                pictureOfDay.title
            )
        } else {
            imageView.setImageResource(R.drawable.ic_connection_error)
            imageView.contentDescription =
                imageView.context.getString(R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet)
            Toast.makeText(imageView.context, "PictureOfDay return video type", Toast.LENGTH_SHORT)
                .show()
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

@BindingAdapter("listData")
fun bindListData(recyclerView: RecyclerView, asteroids: List<Asteroid>?) {
    asteroids?.let {
        if (asteroids.isNotEmpty()) {
            (recyclerView.adapter as AsteroidAdapter).submitList(asteroids)
        }
    }
}



