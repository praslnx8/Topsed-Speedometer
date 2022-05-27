package com.bike.race.ui.driveReport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bike.race.R
import com.bike.race.components.SingleLocationProvider
import com.bike.race.databinding.FragmentDriveMapViewBinding
import com.bike.race.domain.MapType
import com.bike.race.domain.MapType.NORMAL
import com.bike.race.domain.MapType.SATELLITE
import com.bike.race.uiModels.DriveReportData
import com.bike.race.uiModels.DriveReportMapData
import com.bike.race.utils.ConsoleLog
import com.bike.race.utils.observeOnce
import com.bike.race.utils.toLocationPoint
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DriveMapFragment : Fragment() {

    private var googleMap: GoogleMap? = null
    private val viewModel: DriveReportViewModel by sharedViewModel()
    private lateinit var viewBinding: FragmentDriveMapViewBinding
    private val singleLocationProvider: SingleLocationProvider by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentDriveMapViewBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.loadingLayout.visibility = View.VISIBLE

        viewBinding.mapView.onCreate(savedInstanceState)
        viewBinding.mapView.getMapAsync { googleMap ->
            this.googleMap = googleMap
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style_normal)
            )
            viewModel.getDriveReportMapLiveData(
                singleLocationProvider.getLocation(requireContext())?.toLocationPoint()
            ).observeOnce(viewLifecycleOwner) {
                viewBinding.loadingLayout.visibility = View.GONE
                handleMapOperations(it)
            }
        }
        viewModel.getDriveReportLiveData().observeOnce(viewLifecycleOwner) {
            renderSpeedBarValue(it)
        }


        viewBinding.mapTypeButton.setOnClickListener {
            val mapType = viewModel.toggleMapType()
            setMapType(mapType)
        }
    }

    private fun renderSpeedBarValue(driveReportData: DriveReportData?) {
        driveReportData ?: return
        viewBinding.topSpeedBar.text = driveReportData.getTopSpeedAsString()
        viewBinding.avgSpeedBar.text = driveReportData.getAverageSpeedAsString()
        viewBinding.topSpeedBar.max = driveReportData.getTopSpeed().toInt()
        viewBinding.topSpeedBar.progress = driveReportData.getTopSpeed().toFloat()
        viewBinding.avgSpeedBar.max = driveReportData.getTopSpeed().toInt()
        viewBinding.avgSpeedBar.progress = driveReportData.getAverageSpeed().toFloat()
    }

    private fun handleMapOperations(driveReportMapData: DriveReportMapData) {
        googleMap?.clear()
        plotRoute(driveReportMapData.getMapPolyLineOptionList())
        plotMarkers(
            driveReportMapData.getSourcePointMarker(),
            driveReportMapData.getDestPointMarker(),
            driveReportMapData.getTopSpeedPoint()
        )
        moveGoogleMap(
            driveReportMapData.getLatLngBounds(),
            driveReportMapData.getLatLngToZoom(),
            driveReportMapData.getHeading()
        )
        setMapType(viewModel.getMapType())
    }

    private fun setMapType(preferredMapType: MapType) {
        when (preferredMapType) {
            NORMAL -> {
                googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
            SATELLITE -> {
                googleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
            }
        }
    }

    private fun plotMarkers(
        startPointMarkerOptions: MarkerOptions?,
        endPointMarkerOptions: MarkerOptions?,
        topSpeedMarkerOptions: MarkerOptions?
    ) {

        if (startPointMarkerOptions != null) {
            googleMap?.addMarker(startPointMarkerOptions)
        }

        if (endPointMarkerOptions != null) {
            googleMap?.addMarker(endPointMarkerOptions)
        }

        if (topSpeedMarkerOptions != null) {
            googleMap?.addMarker(topSpeedMarkerOptions)
        }
    }

    private fun plotRoute(polylineOptionsList: List<PolylineOptions>) {
        try {
            for (polylineOptions in polylineOptionsList) {
                googleMap?.addPolyline(polylineOptions)
            }
        } catch (ignore: Exception) {
            ConsoleLog.e(e = ignore)
        }
    }

    private fun moveGoogleMap(latLngBounds: LatLngBounds?, latLngToZoom: LatLng?, heading: Double) {
        try {
            latLngBounds?.let {
                val cameraUpdate =
                    CameraUpdateFactory.newLatLngBounds(latLngBounds, 180)
                googleMap?.moveCamera(cameraUpdate)
            } ?: let {
                latLngToZoom?.let {
                    val cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(latLngToZoom, 16f)
                    googleMap?.moveCamera(cameraUpdateFactory)
                }
            }
            googleMap?.cameraPosition?.let {
                val cameraPosition =
                    CameraPosition.builder(it)
                        .bearing(heading.toFloat()).build()
                googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        } catch (ignore: Exception) {
            ConsoleLog.e(e = ignore)
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

    override fun onStop() {
        super.onStop()
        viewBinding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        viewBinding.mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding.mapView.onDestroy()
    }
}