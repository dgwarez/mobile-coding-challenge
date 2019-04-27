package com.traderevchallenge.avikd.traderevchallenge.callbackinterfaces

/**
 * OnSnapPositionChangeListener
 *Interface with onSnapPositionChangeMethod used as a callback for detecting position changes on recyclerView snap
 */

interface OnSnapPositionChangeListener {
    fun onSnapPositionChange(position: Int)
}