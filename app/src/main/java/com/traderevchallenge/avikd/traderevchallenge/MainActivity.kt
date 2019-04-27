package com.traderevchallenge.avikd.traderevchallenge


import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.traderevchallenge.avikd.traderevchallenge.adapter.StaggeredAdapter
import com.traderevchallenge.avikd.traderevchallenge.callbackinterfaces.OnSnapPositionChangeListener
import com.traderevchallenge.avikd.traderevchallenge.dialogfragment.PhotoDetailsDialogFragment
import com.traderevchallenge.avikd.traderevchallenge.models.PhotoByIdAPIBase
import com.traderevchallenge.avikd.traderevchallenge.network.ApiResponse
import com.traderevchallenge.avikd.traderevchallenge.network.Status
import com.traderevchallenge.avikd.traderevchallenge.recyclerviewpagesnaphelper.SnapOnScrollListener
import com.traderevchallenge.avikd.traderevchallenge.snapviewlistenerextension.attachSnapHelperWithListener
import com.traderevchallenge.avikd.traderevchallenge.utils.ApiKeyProvider
import com.traderevchallenge.avikd.traderevchallenge.viewmodels.PhotosViewModel
import com.traderevchallenge.avikd.traderevchallenge.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.annotations.Nullable
import javax.inject.Inject

/**
 * MainActivity
 * This is the main activity that is loaded for the first time the app is launched. This contains the main photo Grid
 * and single photo layout.
 */
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    //Initializing instance variables
    private lateinit var photosViewModel: PhotosViewModel
    lateinit var snapHelper: PagerSnapHelper
    var scrollToPositionInGrid: Int = 0
    var isGridVisible = true
    private val KEY_RECYCLER_STATE = "recycler_state"
    private var mBundleRecyclerViewState: Bundle? = null
    private var mListState: Parcelable? = null

    /**
     * onCreate
     * Perform injections and initialize observers
     */
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        //Initialize Dependency Injection
        (application as MyApplication).appComponent.doInjection(this)
        //Initialize view model
        photosViewModel = ViewModelProviders.of(this, viewModelFactory).get(PhotosViewModel::class.java!!)
        //Initialize snapHelper for recycler view snapping in full screen image view
        snapHelper = PagerSnapHelper()
        hitPhotosAPIAndObserve()
        initPhotoByIdObserver()
    }

    /**
     * hitPhotosAPIAndObserve
     * This method is used to initialize listLiveData observer to observe observables generate by a service call to the
     * unsplash photos API. The data source is a androidx PagedListAdapter to enable paginated requests. The response
     * is render by renderSuccessResponse method
     */

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

    /**
     * initPhotoByIdObserver
     * This method is used to initialize mutableLiveData observer to observe observables generate by a service call to
     * the unsplash photo detail by photo ID API. The response is displayed through the renderPhotoByApiResponse method
     */
    private fun initPhotoByIdObserver() {
        photosViewModel.progressLiveStatus.observe(this, Observer {
            when {
                (it as ApiResponse<Any?>).status == Status.LOADING -> {
                    indeterminateBar.visibility = View.VISIBLE
                }
                it.status == Status.SUCCESS -> {
                    indeterminateBar.visibility = View.GONE
                    renderPhotoByApiResponse(it.data as PhotoByIdAPIBase)
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
    }

    /**
     * setupGridClickListener
     * This method is used to initialize the onclick listener for the Grid. This on click listener switches to single
     * photo layout if grid is being displayed. If single photo layout is being displayed, it calls the photo details
     * by ID api to get more information on the photo
     */
    private fun setupGridClickListener() {
        (photoGrid.adapter as StaggeredAdapter).setOnPhotoClickedListener(object :
            StaggeredAdapter.OnPhotoClickedListener {
            override fun onPhotoClicked(photoId: String?, position: Int) {
                if (isGridVisible) {
                    scrollToPositionInGrid = position
                    showSlidingRecyclerViewPager()
                } else {
                    photosViewModel.hitPhotosByIdApi(this@MainActivity, photoId)
                }
            }

        })
    }

    /**
     * renderSuccessResponse
     * If the screen is being loaded for the first time this method calls the showGrid function to initialize and display
     * the grid
     */
    private fun renderSuccessResponse() {
        if (photosViewModel.firstLoad) {
            showGrid()
            photosViewModel.firstLoad = false
        }
        setupGridClickListener()
    }


    /**
     * renderPhotoByApiResponse
     * This method is used to render results from the photos by id api
     * @param photoByIdAPIBase
     */
    private fun renderPhotoByApiResponse(photoByIdAPIBase: PhotoByIdAPIBase) {
        val dialogFragment = PhotoDetailsDialogFragment()
        val args = Bundle()
        args.putString("description", photoByIdAPIBase.description)
        args.putString("alt_description", photoByIdAPIBase.alt_description)
        args.putString("user_name", photoByIdAPIBase.user?.name)
        args.putString("camera_details", photoByIdAPIBase.exif.toString())
        args.putString("photo_url", photoByIdAPIBase.urls?.small)
        dialogFragment.arguments = args
        dialogFragment.show(supportFragmentManager, "Photo Details Fragment")
    }

    /**
     * isAPIKeyAvailable
     * This method is used to check if API key is available in res -> assets -> app.properties
     * @return true if API key is available
     */
    private fun isAPIKeyAvailable(): Boolean {
        return if (ApiKeyProvider.fetchApiKey(applicationContext) == "") {
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

    /**
     * onBackPressed
     * If user hits back in single photo view, this takes user back to grid view
     * Default behaviour if grid is already being displayed
     */
    override fun onBackPressed() {
        if (!isGridVisible)
            showGrid()
        else
            super.onBackPressed()
    }

    /**
     * showGrid
     * This method is used to initialize and show the main Grid view using a Staggered grid layout
     */
    private fun showGrid() {
        isGridVisible = true
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        photoGrid.layoutManager = layoutManager
        photoGrid.setHasFixedSize(true)
        photosViewModel.firstLoad = false
        photoGrid.layoutManager = layoutManager
        snapHelper.attachToRecyclerView(null)
        //scroll to last selection position
        photoGrid.scrollToPosition(scrollToPositionInGrid)
    }

    /**
     * showGrid
     * This method is used to initialize and show the full screen photo view layout. It uses the same data adapter as
     * the initial grid layout and changes the layout manager from staggered to linear horizontal.
     */
    private fun showSlidingRecyclerViewPager() {
        isGridVisible = false
        val layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        photoGrid.layoutManager = layoutManager
        photoGrid.scrollToPosition(scrollToPositionInGrid)
        snapHelper.attachToRecyclerView(photoGrid)
        //Attaches snap helper to get a call on view span during left and right swipe. This tracks the current
        //position on the grid
        photoGrid.attachSnapHelperWithListener(
            snapHelper,
            SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
            object : OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    scrollToPositionInGrid = position
                }

            })
    }

    /**
     * onSaveInstanceState
     * This is used to save instance state. This is used to restore state during orientation changes
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        mListState = photoGrid.layoutManager?.onSaveInstanceState()
        mBundleRecyclerViewState?.putParcelable(KEY_RECYCLER_STATE, mListState)
    }

    /**
     * onSaveInstanceState
     * callback for orientation change. This is used to handle orientation changes in the grid or single photo view
     * layout. The saved instance state is restored and the grid/photo view is reloaded
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (mBundleRecyclerViewState != null) {
            Handler().postDelayed({
                mListState = (mBundleRecyclerViewState as Bundle).getParcelable(KEY_RECYCLER_STATE)
                photoGrid.layoutManager?.onRestoreInstanceState(mListState)
            }, 50)

        }
        if (isGridVisible) {
            showGrid()
        } else {
            showSlidingRecyclerViewPager()
        }
    }
}