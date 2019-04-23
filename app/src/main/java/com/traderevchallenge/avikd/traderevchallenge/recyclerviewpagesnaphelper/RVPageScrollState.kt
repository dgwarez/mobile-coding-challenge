package com.traderevchallenge.avikd.traderevchallenge.recyclerviewpagesnaphelper

sealed class RVPageScrollState {
    class Idle: RVPageScrollState()
    class Dragging: RVPageScrollState()
    class Settling: RVPageScrollState()
}