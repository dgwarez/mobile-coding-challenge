package com.traderevchallenge.avikd.traderevchallenge.adapter

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.traderevchallenge.avikd.traderevchallenge.models.PhotosBase
import com.traderevchallenge.avikd.traderevchallenge.viewholder.StaggeredPhotosViewHolder


var DIFF_CALLBACK: DiffUtil.ItemCallback<PhotosBase> = object : DiffUtil.ItemCallback<PhotosBase>() {
    override fun areItemsTheSame(oldItem: PhotosBase, newItem: PhotosBase): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PhotosBase, newItem: PhotosBase): Boolean {
        return oldItem.id == newItem.id
    }
}

class StaggeredAdapter(/*private val retry: () -> Unit*/) :
    PagedListAdapter<PhotosBase, StaggeredPhotosViewHolder>(DIFF_CALLBACK) {
    var onItemClickListener: StaggeredAdapter.OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaggeredPhotosViewHolder {
        return StaggeredPhotosViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: StaggeredPhotosViewHolder, position: Int) {
        if (itemCount >0 ) {
            holder.bind(getItem(position))
            holder.itemView.setOnClickListener(View.OnClickListener { mBluetoothClickListener?.onBluetoothDeviceClicked(getItem(position)?.id?.toInt()) })
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

    interface OnBluetoothDeviceClickedListener {
        fun onBluetoothDeviceClicked(photoId: Int?)
    }

    private var mBluetoothClickListener: OnBluetoothDeviceClickedListener? = null

    fun setOnBluetoothDeviceClickedListener(listener: OnBluetoothDeviceClickedListener) {
        mBluetoothClickListener = listener
    }
}


