package com.traderevchallenge.avikd.traderevchallenge


import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.traderevchallenge.avikd.traderevchallenge.adapter.StaggeredAdapter
import com.traderevchallenge.avikd.traderevchallenge.models.PhotosBase
import com.traderevchallenge.avikd.traderevchallenge.network.ApiResponse
import com.traderevchallenge.avikd.traderevchallenge.network.Status
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
    //private lateinit var favPlaces: RecyclerView

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        showPopularPhotosButton.setOnClickListener { view -> onClick(view) }
        (application as MyApplication).appComponent.doInjection(this)
        photosViewModel = ViewModelProviders.of(this, viewModelFactory).get(PhotosViewModel::class.java!!)
        //favPlaces = findViewById<View>(R.id.photoGrid) as RecyclerView

    }

    fun onClick(view: View) {
        if (view.id == R.id.showPopularPhotosButton) {
            onDisplayPhotosClick()
        }
    }

    internal fun onDisplayPhotosClick() {
        if (isAPIKeyAvailable()) {
            photosViewModel.listLiveData.observe(this, androidx.lifecycle.Observer {
                photoGrid.adapter = StaggeredAdapter()
                (photoGrid.adapter as StaggeredAdapter).submitList(it)
            })

            photosViewModel.progressLoadStatus.observe(this, androidx.lifecycle.Observer {
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

    /*
* method to handle success response
* */
    private fun renderSuccessResponse() {
        if (photosViewModel.firstLoad) {
            val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            photoGrid.visibility = View.VISIBLE
            showPopularPhotosButton.visibility = View.GONE
            photoGrid.layoutManager = layoutManager
            photoGrid.setHasFixedSize(true)
            photosViewModel.firstLoad = false
        }
        //favPlaces.adapter?.notifyDataSetChanged()
        (photoGrid.adapter as StaggeredAdapter).setOnBluetoothDeviceClickedListener(object :
            StaggeredAdapter.OnBluetoothDeviceClickedListener {
            override fun onPhotoClicked(photoId: String?) {
                startFullPhotoDisplayActivity(photoId)
                Log.d("TradeRevChallengeTest", photoId.toString())
            }
        })
    }

    private fun startFullPhotoDisplayActivity(photoId: String?) {
        if (isAPIKeyAvailable()) {
            val intent = Intent(this@MainActivity, FullPhotoDisplayActivity::class.java)
            intent.putExtra("photoId", photoId)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
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