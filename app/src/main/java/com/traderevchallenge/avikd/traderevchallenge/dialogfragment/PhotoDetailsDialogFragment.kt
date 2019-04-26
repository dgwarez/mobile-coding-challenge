package com.traderevchallenge.avikd.traderevchallenge.dialogfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.traderevchallenge.avikd.traderevchallenge.R
import kotlinx.android.synthetic.main.fragment_photo_details_dialog.view.*


class PhotoDetailsDialogFragmentent : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_photo_details_dialog, container, false)
        rootView.title.text = String.format(getString(R.string.alt_description_header,arguments?.getString("alt_description")))
        rootView.user_name.text = String.format(getString(R.string.user_name_header,arguments?.getString("user_name")))
        rootView.details.text = String.format(getString(R.string.camera_exit_data_header,arguments?.getString("camera_details")))
        val url = arguments?.getString("photo_url")
        if (url != null) {
            Glide.with(context)
                .load(url)
                .apply(RequestOptions.placeholderOf(R.drawable.download))
                .into(rootView.image)
        } else {
            Glide.with(context).clear(rootView.image)
            rootView.image.setImageDrawable(null)
        }

        rootView.dismiss.setOnClickListener(View.OnClickListener { dismiss() })
        return rootView
    }
}