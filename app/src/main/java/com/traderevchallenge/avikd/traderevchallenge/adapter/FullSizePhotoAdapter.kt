package com.traderevchallenge.avikd.traderevchallenge.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.traderevchallenge.avikd.traderevchallenge.models.PhotosBase
import com.traderevchallenge.avikd.traderevchallenge.viewholder.FullSizePhotosViewHolder
var DIFF_CALLBACK_FULL: DiffUtil.ItemCallback<PhotosBase> = object : DiffUtil.ItemCallback<PhotosBase>() {
    override fun areItemsTheSame(oldItem: PhotosBase, newItem: PhotosBase): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PhotosBase, newItem: PhotosBase): Boolean {
        return oldItem.id == newItem.id
    }
}

class FullSizePhotoAdapter :
    PagedListAdapter<PhotosBase, FullSizePhotosViewHolder>(DIFF_CALLBACK_FULL) {
    var onItemClickListener: StaggeredAdapter.OnItemClickListener? = null
    private var mInflater: LayoutInflater? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullSizePhotosViewHolder {
        return FullSizePhotosViewHolder.create(mInflater, parent)
    }

    override fun onBindViewHolder(holder: FullSizePhotosViewHolder, position: Int) {
        if (itemCount >0 ) {
            holder.bind(getItem(position))
            holder.itemView.setOnClickListener(View.OnClickListener { mBluetoothClickListener?.onPhotoClicked(getItem(position)?.id) })
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
        fun onPhotoClicked(photoId: String?)
    }

    private var mBluetoothClickListener: OnBluetoothDeviceClickedListener? = null

    fun setOnBluetoothDeviceClickedListener(listener: OnBluetoothDeviceClickedListener) {
        mBluetoothClickListener = listener
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mInflater = LayoutInflater.from(recyclerView.context)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mInflater = null
    }
}