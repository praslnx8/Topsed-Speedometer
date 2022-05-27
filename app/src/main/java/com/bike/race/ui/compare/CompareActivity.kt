package com.bike.race.ui.compare

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bike.race.R
import com.bike.race.databinding.ActivityCompareBinding
import com.bike.race.uiModels.ComparableDriveData
import com.bike.race.uiModels.CompareData
import com.bike.race.utils.MarkerUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class CompareActivity : AppCompatActivity() {

    private val viewModel: CompareViewModel by viewModel()
    private var googleMap: GoogleMap? = null
    private lateinit var viewBinding: ActivityCompareBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCompareBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setSupportActionBar(viewBinding.toolbar)
        viewBinding.mapView.onCreate(savedInstanceState)

        val currentDriveId = intent.getLongExtra(CURRENT_DRIVE_ID_KEY, -1L)
        val compareDriveId = intent.getLongExtra(COMPARE_DRIVE_ID_KEY, -1L)

        viewModel.compareLiveData.observe(this) {
            showData(it)
        }

        viewBinding.mapView.getMapAsync {
            this.googleMap = it
            googleMap?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this,
                    R.raw.map_style_normal
                )
            )
            googleMap?.setPadding(0, 120, 0, 0)
            viewModel.fetchDataToCompare(
                currentDriveId,
                compareDriveId
            )
        }

        supportFragmentManager.beginTransaction()
            .add(viewBinding.container.id, CompareDataFragment()).commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun showData(data: CompareData) {

        val polyline1 = data.comparableDriveData1.getPolyLine(getColor(R.color.mapPolylineBlue))
        val polyline2 = data.comparableDriveData2?.getPolyLine(getColor(R.color.mapPolylineRed))
        val latLngBounds = data.getLatLngBounds()
        val stopMarker = data.comparableDriveData1.getStopMarker()

        googleMap?.addPolyline(polyline1)
        if (polyline2 != null) {
            googleMap?.addPolyline(polyline2)
        }
        stopMarker?.let {
            googleMap?.addMarker(stopMarker)
        }

        if (latLngBounds != null) {
            try {
                moveCamera(latLngBounds)
                rotateCamera(data.comparableDriveData1.getHeading())
            } catch (ignore: Exception) {
            }
        }

        plotMarker(null, data.comparableDriveData1, R.drawable.ic_marker_bike_blue, 0L)
        if (data.comparableDriveData2 != null) {
            plotMarker(null, data.comparableDriveData2, R.drawable.ic_marker_bike_red, 0L)
        }
    }

    private fun moveCamera(latLngBounds: LatLngBounds) {
        try {
            val cameraUpdate =
                CameraUpdateFactory.newLatLngBounds(latLngBounds, 40)
            googleMap?.moveCamera(cameraUpdate)
            googleMap?.moveCamera(CameraUpdateFactory.zoomOut())
        } catch (e: Exception) {
            val cameraUpdate =
                CameraUpdateFactory.newLatLngBounds(latLngBounds, 40, 40, 40)
            googleMap?.moveCamera(cameraUpdate)
            googleMap?.moveCamera(CameraUpdateFactory.zoomOut())
        }
    }

    private fun rotateCamera(heading: Double) {
        val googleMapCameraPosition = googleMap?.cameraPosition ?: return
        val cameraPosition =
            CameraPosition.builder(googleMapCameraPosition)
                .bearing(heading.toFloat()).build()
        googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun plotMarker(
        markerPosition: Marker?,
        comparableDriveData: ComparableDriveData,
        @DrawableRes drawableIcon: Int,
        time: Long
    ) {
        var marker = markerPosition
        if (marker == null) {
            comparableDriveData.getStartingMarkerPosition(
                BitmapDescriptorFactory.fromResource(drawableIcon)
            )?.let {
                marker = googleMap?.addMarker(it)
            }
        }

        marker?.let {
            comparableDriveData.getNextLocationWithTime(time)?.let {
                MarkerUtil.animateMarker(
                    googleMap,
                    marker,
                    LatLng(it.lat, it.lon),
                    (it.time - time)
                ) {
                    plotMarker(marker, comparableDriveData, drawableIcon, it.time)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewBinding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewBinding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        viewBinding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding.mapView.onDestroy()
    }

    override fun onBackPressed() {
        if (viewModel.compareLiveData.value != null) {
            setResult(RESULT_OK)
        }
        super.onBackPressed()
    }

    companion object {

        private const val CURRENT_DRIVE_ID_KEY = "current_drive_id"
        private const val COMPARE_DRIVE_ID_KEY = "compare_drive_id"

        fun open(
            context: Context,
            currentDriveId: Long,
            compareDriveId: Long,
        ) {
            val intent = Intent(context, CompareActivity::class.java)
            intent.putExtra(CURRENT_DRIVE_ID_KEY, currentDriveId)
            intent.putExtra(COMPARE_DRIVE_ID_KEY, compareDriveId)
            context.startActivity(intent)
        }
    }
}