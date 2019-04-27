package com.traderevchallenge.avikd.traderevchallenge.adapter

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.traderevchallenge.avikd.traderevchallenge.models.PhotosBase
import com.traderevchallenge.avikd.traderevchallenge.viewholder.StaggeredPhotosViewHolder

/**
 * DIFF_CALLBACK
 * DIFF_CALLBACK is used to detect changes in items in paginated lists and load data accordingly
 * In this case each item is uniquely identified with the photo id
 */
var DIFF_CALLBACK: DiffUtil.ItemCallback<PhotosBase> = object : DiffUtil.ItemCallback<PhotosBase>() {
    override fun areItemsTheSame(oldItem: PhotosBase, newItem: PhotosBase): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PhotosBase, newItem: PhotosBase): Boolean {
        return oldItem.id == newItem.id
    }
}

/**
 * StaggeredAdapter
 * This is the data adapter class for the photogrid layout
 */
class StaggeredAdapter :
    PagedListAdapter<PhotosBase, StaggeredPhotosViewHolder>(DIFF_CALLBACK) {
    var onItemClickListener: StaggeredAdapter.OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaggeredPhotosViewHolder {
        return StaggeredPhotosViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: StaggeredPhotosViewHolder, position: Int) {
        if (itemCount >0 ) {
            holder.bind(getItem(holder.adapterPosition))
            holder.itemView.setOnClickListener(View.OnClickListener { mPhotoClickListener?.onPhotoClicked(getItem(holder.adapterPosition)?.id, position) })
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnPhotoClickedListener {
        fun onPhotoClicked(photoId: String?, position: Int)
    }

    private var mPhotoClickListener: OnPhotoClickedListener? = null

    fun setOnPhotoClickedListener(listener: OnPhotoClickedListener) {
        mPhotoClickListener = listener
    }
}


