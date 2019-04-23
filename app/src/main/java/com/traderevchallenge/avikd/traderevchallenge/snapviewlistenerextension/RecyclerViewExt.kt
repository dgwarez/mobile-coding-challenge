package com.traderevchallenge.avikd.traderevchallenge.snapviewlistenerextension

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.traderevchallenge.avikd.traderevchallenge.callbackinterfaces.OnSnapPositionChangeListener
import com.traderevchallenge.avikd.traderevchallenge.recyclerviewpagesnaphelper.SnapOnScrollListener

fun RecyclerView.attachSnapHelperWithListener(
    snapHelper: SnapHelper,
    behavior: SnapOnScrollListener.Behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
    onSnapPositionChangeListener: OnSnapPositionChangeListener
) {
    snapHelper.attachToRecyclerView(this)
    val snapOnScrollListener = SnapOnScrollListener(snapHelper, behavior, onSnapPositionChangeListener)
    addOnScrollListener(snapOnScrollListener)
}