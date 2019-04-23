package com.traderevchallenge.avikd.traderevchallenge

import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.os.Bundle
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

class FullPhotoDisplayActivity : AppCompatActivity() {
    var photoId: String? = ""
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fullPhotoDisplayViewModel: FullPhotoDisplayViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        this.supportActionBar?.hide()
        photoId = intent.getStringExtra("photoId")
        val pageNumber = 1
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
        if (isAPIKeyAvailable()) {
            fullPhotoDisplayViewModel.listLiveData.observe(this, androidx.lifecycle.Observer {
                recyclerViewViewPager.layoutManager = LinearLayoutManager(this@FullPhotoDisplayActivity, LinearLayoutManager.HORIZONTAL, false)
                recyclerViewViewPager.adapter = FullSizePhotoAdapter()
                val snapHelper = PagerSnapHelper()
                snapHelper.attachToRecyclerView(recyclerViewViewPager)
                recyclerViewViewPager.attachSnapHelperWithListener(snapHelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE, object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                        println(position)
                    }
                })
                /*RVPagerSnapHelperListenable().attachToRecyclerView(
                    recyclerViewViewPager,
                    object : RVPagerStateListener {
                        override fun onPageScroll(pagesState: List<VisiblePageState>) {
                            for (pageState in pagesState) {
                                val vh = recyclerViewViewPager.findContainingViewHolder(pageState.view)
                                //vh.setRealtimeAttr(pageState.index, pageState.viewCenterX.toString(), pageState.distanceToSettled, pageState.distanceToSettledPixels)
                            }
                        }

                        override fun onScrollStateChanged(state: RVPageScrollState) {
                        }

                        override fun onPageSelected(index: Int) {
                            val vh = recyclerViewViewPager.findViewHolderForAdapterPosition(index)
                            //vh?.onSelected()
                        }
                    })*/
                (recyclerViewViewPager.adapter as FullSizePhotoAdapter).submitList(it)
            })

            fullPhotoDisplayViewModel.progressLoadStatus.observe(this, androidx.lifecycle.Observer {
                when {
                    (it as ApiResponse<Any?>).status == Status.LOADING -> {

                    }
                    it.status == Status.SUCCESS -> {
                        renderSuccessResponse()
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
                (it as ApiResponse<Any?>).status == Status.LOADING -> {

                }
                it.status == Status.SUCCESS -> {
                    renderSuccessResponse()
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

    private fun renderSuccessResponse() {
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
}
