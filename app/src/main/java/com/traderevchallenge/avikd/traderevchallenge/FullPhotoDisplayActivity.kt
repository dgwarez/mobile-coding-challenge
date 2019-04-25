package com.traderevchallenge.avikd.traderevchallenge

import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.traderevchallenge.avikd.traderevchallenge.adapter.FullSizePhotoAdapter
import com.traderevchallenge.avikd.traderevchallenge.callbackinterfaces.OnSnapPositionChangeListener
import com.traderevchallenge.avikd.traderevchallenge.network.ApiResponse
import com.traderevchallenge.avikd.traderevchallenge.network.Status
import com.traderevchallenge.avikd.traderevchallenge.recyclerviewpagesnaphelper.SnapOnScrollListener
import com.traderevchallenge.avikd.traderevchallenge.snapviewlistenerextension.attachSnapHelperWithListener
import com.traderevchallenge.avikd.traderevchallenge.utils.ApiKeyProvider
import com.traderevchallenge.avikd.traderevchallenge.viewmodels.FullPhotoDisplayViewModel
import com.traderevchallenge.avikd.traderevchallenge.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.activity_full_photo_display.*
import javax.inject.Inject
import android.content.Intent
import android.provider.ContactsContract
import android.view.View
import com.traderevchallenge.avikd.traderevchallenge.appconstants.AppConstants.PAGINATION_NO_OF_ITEMS_ON_SINGLE_PAGE
import com.traderevchallenge.avikd.traderevchallenge.models.PhotoByIdAPIBase
import com.traderevchallenge.avikd.traderevchallenge.models.PhotosBase


class FullPhotoDisplayActivity : AppCompatActivity() {
    var photoId: String? = ""
    var position: Int = 0
    var scrollToPosition: Int = 0
    var requestCode: Int = 0
    var onScrollChangedPageNoTracker: Int = 0
    var onScrollChangedPositionTracker: Int = 0
    var preivousPosition: Int = 0
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fullPhotoDisplayViewModel: FullPhotoDisplayViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        this.supportActionBar?.hide()
        photoId = intent.getStringExtra("photoId")
        position = intent.getIntExtra("position", 0)
        requestCode = intent.getIntExtra("requestCode", 0)
        onScrollChangedPositionTracker = position
        var pageNoCalculated = (position / PAGINATION_NO_OF_ITEMS_ON_SINGLE_PAGE) + 1
        scrollToPosition = position % PAGINATION_NO_OF_ITEMS_ON_SINGLE_PAGE
        if (pageNoCalculated == 0) {
            MyApplication.currentPageNumber = 1
        } else {
            MyApplication.currentPageNumber = pageNoCalculated
        }
        preivousPosition = position
        onScrollChangedPageNoTracker = MyApplication.currentPageNumber
        setContentView(R.layout.activity_full_photo_display)
        //Setup recyclerView viewPager
        (application as MyApplication).appComponent.doInjectionPhotos(this)
        fullPhotoDisplayViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(FullPhotoDisplayViewModel::class.java!!)
        fullPhotoDisplayViewModel.mPhotoId = photoId
        //fullPhotoDisplayViewModel.hitPhotosByIdApi(this)
        //initObserver()
        initializePageList()
    }

    private fun initializePageList() {
        fullPhotoDisplayViewModel.listLiveData.value?.dataSource?.invalidate()
        if (isAPIKeyAvailable()) {
            Log.d("Avik: ini Swiperight", "Swiperight!")
            Log.d("Avik: ini Position", position.toString())
            Log.d("Avik:iniscrollToMainGr", onScrollChangedPositionTracker.toString())
            recyclerViewViewPager.adapter = FullSizePhotoAdapter()
            fullPhotoDisplayViewModel.listLiveData.observe(this, androidx.lifecycle.Observer {
                recyclerViewViewPager.layoutManager =
                    LinearLayoutManager(this@FullPhotoDisplayActivity, LinearLayoutManager.HORIZONTAL, false)
                val snapHelper = PagerSnapHelper()
                (recyclerViewViewPager.adapter as FullSizePhotoAdapter).submitList(it)
                snapHelper.attachToRecyclerView(recyclerViewViewPager)
                recyclerViewViewPager.attachSnapHelperWithListener(
                    snapHelper,
                    SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
                    object : OnSnapPositionChangeListener {
                        override fun onSnapPositionChange(position: Int) {
                            Log.d("Avik: RecyclerViewsnap", position.toString())
                            //Moving forward to right
                            //Moving backward to left
                            if (preivousPosition < position) {
                                if (position - preivousPosition == 1) {
                                    onScrollChangedPositionTracker++
                                    if (position % PAGINATION_NO_OF_ITEMS_ON_SINGLE_PAGE == 0) {
                                        onScrollChangedPageNoTracker++
                                        Log.d("Avik: scrollToPage", onScrollChangedPageNoTracker.toString())
                                    }
                                    Log.d("Avik: Swiperight", "Swiperight!")
                                    Log.d("Avik: Position", position.toString())
                                    Log.d("Avik: scrollToMainGrid", onScrollChangedPositionTracker.toString())
                                } else {
                                    //This is page change to the left - A new page has been loaded updating listsize
                                    if (onScrollChangedPageNoTracker > 0) onScrollChangedPageNoTracker--
                                    Log.d("Avik: Special Swipeleft", "Swipeleft!")
                                    Log.d("Avik: Position", position.toString())
                                    Log.d("Avik: scrollToPage", onScrollChangedPageNoTracker.toString())
                                    Log.d("Avik: scrollToMainGrid", onScrollChangedPositionTracker.toString())
                                }
                            } else if (preivousPosition > position) {
                                if (preivousPosition - position == 1) {
                                    onScrollChangedPositionTracker--
                                    if (position % PAGINATION_NO_OF_ITEMS_ON_SINGLE_PAGE == 0) {
                                        if (onScrollChangedPageNoTracker > 0) onScrollChangedPageNoTracker--
                                        Log.d("Avik: scrollToPage", onScrollChangedPageNoTracker.toString())
                                    }
                                    Log.d("Avik: Swipeleft", "Swipeleft!")
                                    Log.d("Avik: Position", position.toString())
                                    Log.d("Avik: scrollToMainGrid", onScrollChangedPositionTracker.toString())
                                }
                            }
                            preivousPosition = position
                        }
                    })
            })

            fullPhotoDisplayViewModel.progressLoadStatus.observe(this, androidx.lifecycle.Observer {
                when {
                    it.status == Status.LOADING -> {

                    }
                    it.status == Status.SUCCESS -> {
                        renderSuccessResponse(it.data as List<PhotosBase>)
                    }
                    it.status == Status.COMPLETED -> {

                    }
                    it.status == Status.ERROR -> {
                        Toast.makeText(
                            this@FullPhotoDisplayActivity,
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

    private fun initObserver() {
        fullPhotoDisplayViewModel.progressLiveStatus.observe(this, Observer {
            when {
                it.status == Status.LOADING -> {

                }
                it.status == Status.SUCCESS -> {
                    renderInfoResponse(it.data as PhotoByIdAPIBase)
                }
                it.status == Status.COMPLETED -> {

                }
                it.status == Status.ERROR -> {
                    Toast.makeText(
                        this@FullPhotoDisplayActivity,
                        resources.getString(R.string.error_string),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
    private fun renderInfoResponse(photoByIdAPIBase: PhotoByIdAPIBase) {
        Log.e("Photo desc",photoByIdAPIBase.alt_description?:photoByIdAPIBase.description?:"")
    }
    private fun renderSuccessResponse(photosBaseList: List<PhotosBase>) {
        initObserver()
        if (fullPhotoDisplayViewModel.firstLoad) {
            Log.d("Avik: scrollToPositionF", scrollToPosition.toString())
            recyclerViewViewPager.scrollToPosition(scrollToPosition)
            fullPhotoDisplayViewModel.firstLoad = false
        }
        (recyclerViewViewPager.adapter as FullSizePhotoAdapter).setOnBluetoothDeviceClickedListener(object :
            FullSizePhotoAdapter.OnBluetoothDeviceClickedListener {
            override fun onPhotoClicked(photoId: String?) {
                fullPhotoDisplayViewModel.mPhotoId = photoId
                fullPhotoDisplayViewModel.hitPhotosByIdApi(this@FullPhotoDisplayActivity)
            }
        })
/*        Log.d("Avik", response?.photo?.images?.get(0)?.url)
        val url = response?.photo?.images?.get(0)?.url
        Glide.with(this)
            .load(url)
            .apply(RequestOptions.placeholderOf(R.drawable.download))
            .into(main_image_view)
        titleContent.text = response?.photo?.name
        descriptionContent.text = response?.photo?.description
        cameraContent.text = response?.photo?.camera
        shotInformationContent.text = (response?.photo?.iso?:"") + "  " + (response?.photo?.focal_length?:"") + "  " + (response?.photo?.aperture?:"") +  "  " + (response?.photo?.shutter_speed?:"")*/
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
        val resultIntent = Intent()
        MyApplication.currentPageNumber = (onScrollChangedPositionTracker/PAGINATION_NO_OF_ITEMS_ON_SINGLE_PAGE) + 1
        resultIntent.putExtra("loadInitPageNumber", MyApplication.currentPageNumber)
        resultIntent.putExtra("scrollToPosition", onScrollChangedPositionTracker % PAGINATION_NO_OF_ITEMS_ON_SINGLE_PAGE)
        setResult(requestCode, resultIntent)
        finish()
        super.onBackPressed()
    }
}
