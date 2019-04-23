package com.traderevchallenge.avikd.traderevchallenge.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.traderevchallenge.avikd.traderevchallenge.R
import com.traderevchallenge.avikd.traderevchallenge.models.PhotosBase
import kotlinx.android.synthetic.main.full_screen_photo_layout.view.*

class FullSizePhotosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(photos: PhotosBase?) {
        if (photos != null) {
            itemView.text_desc.text = photos.alt_description
            val url = photos.urls?.small
            Glide.with(itemView.context)
                .load(url)
                .apply(RequestOptions.placeholderOf(R.drawable.download))
                .into(itemView.image)
        } else {
            Glide.with(itemView.context).clear(itemView.image)
            itemView.image.setImageDrawable(null)
        }
    }

    companion object {
        fun create(inflater: LayoutInflater?, parent: ViewGroup): FullSizePhotosViewHolder {
            val view = inflater!!.inflate(R.layout.full_screen_photo_layout, parent, false)
            return FullSizePhotosViewHolder(view)
        }
    }

}