package com.traderevchallenge.avikd.traderevchallenge.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.traderevchallenge.avikd.traderevchallenge.R
import com.traderevchallenge.avikd.traderevchallenge.models.PhotosBase
import kotlinx.android.synthetic.main.staggered_layout.view.*

class StaggeredPhotosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(photos: PhotosBase?) {
        if (photos != null) {
            val url = photos.urls?.regular
            Glide.with(itemView.context)
                .load(url)
                .apply(RequestOptions.placeholderOf(R.drawable.download))
                .into(itemView.photoImageView)
        } else {
            Glide.with(itemView.context).clear(itemView.photoImageView)
            itemView.photoImageView.setImageDrawable(null)
        }
    }

    companion object {
        fun create(parent: ViewGroup): StaggeredPhotosViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.staggered_layout, parent, false)
            return StaggeredPhotosViewHolder(view)
        }
    }

}