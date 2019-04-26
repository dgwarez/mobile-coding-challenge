package com.traderevchallenge.avikd.traderevchallenge


import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.traderevchallenge.avikd.traderevchallenge.adapter.StaggeredAdapter
import com.traderevchallenge.avikd.traderevchallenge.callbackinterfaces.OnSnapPositionChangeListener
import com.traderevchallenge.avikd.traderevchallenge.network.Status
import com.traderevchallenge.avikd.traderevchallenge.recyclerviewpagesnaphelper.SnapOnScrollListener
import com.traderevchallenge.avikd.traderevchallenge.snapviewlistenerextension.attachSnapHelperWithListener
import com.traderevchallenge.avikd.traderevchallenge.utils.ApiKeyProvider
import com.traderevchallenge.avikd.traderevchallenge.viewmodels.PhotosViewModel
import com.traderevchallenge.avikd.traderevchallenge.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.annotations.Nullable
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var photosViewModel: PhotosViewModel
    lateinit var snapHelper: PagerSnapHelper
    var scrollToPositionInGrid: Int = 0

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as MyApplication).appComponent.doInjection(this)
        photosViewModel = ViewModelProviders.of(this, viewModelFactory).get(PhotosViewModel::class.java!!)
        snapHelper = PagerSnapHelper()
        hitPhotosAPIAndObserve()
    }

    private fun hitPhotosAPIAndObserve() {
        if (isAPIKeyAvailable()) {
            photoGrid.adapter = StaggeredAdapter()
            photosViewModel.listLiveData.observe(this, androidx.lifecycle.Observer {
                (photoGrid.adapter as StaggeredAdapter).submitList(it)
            })

            photosViewModel.progressLoadStatus.observe(this, androidx.lifecycle.Observer {
                when {
                    it.status == Status.LOADING -> {
                        indeterminateBar.visibility = View.VISIBLE
                    }
                    it.status == Status.SUCCESS -> {
                        indeterminateBar.visibility = View.GONE
                        renderSuccessResponse()
                    }
                    it.status == Status.COMPLETED -> {

                    }
                    it.status == Status.ERROR -> {
                        indeterminateBar.visibility = View.GONE
                        Toast.makeText(
                            this@MainActivity,
                            resources.getString(R.string.error_string),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        } else {
            return
        }
    }

    private fun setupGridClickListener() {
        (photoGrid.adapter as StaggeredAdapter).setOnBluetoothDeviceClickedListener(object: StaggeredAdapter.OnBluetoothDeviceClickedListener{
            override fun onPhotoClicked(photoId: String?, position: Int) {
                showSlidingRecylerViewPager(photoId, position)
            }

        })
    }
    private fun renderSuccessResponse() {
        if (photosViewModel.firstLoad) {
            showGrid()
            photosViewModel.firstLoad = false
        }
        setupGridClickListener()
    }

    private fun isAPIKeyAvailable(): Boolean {
        return if (ApiKeyProvider.fetchApiKey() == "") {
            val okButton = { dialog: DialogInterface, which: Int -> dialog.cancel() }
            val builder = AlertDialog.Builder(this)
            with(builder)
            {
                setTitle(getText(R.string.api_key_error_header))
                setMessage(getText(R.string.api_key_error))
                setNeutralButton("OK", android.content.DialogInterface.OnClickListener(function = okButton))
                show()
            }
            false
        } else true
    }

    override fun onBackPressed() {
        showGrid()
    }

    private fun showGrid() {
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        photoGrid.layoutManager = layoutManager
        photoGrid.setHasFixedSize(true)
        photosViewModel.firstLoad = false
        photoGrid.layoutManager = layoutManager
        snapHelper.attachToRecyclerView(null)
        photoGrid.scrollToPosition(scrollToPositionInGrid)
    }

    private fun showSlidingRecylerViewPager(photoId: String?, position: Int) {
        val layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        Log.d("TradeRevChallengeTest", photoId.toString())
        photoGrid.layoutManager = layoutManager
        photoGrid.scrollToPosition(position)
        snapHelper.attachToRecyclerView(photoGrid)
        photoGrid.attachSnapHelperWithListener(
            snapHelper,
            SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
            object : OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    scrollToPositionInGrid = position
                }

            })
    }
}