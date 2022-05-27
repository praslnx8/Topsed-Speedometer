package com.bike.race.ui.mydrive

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bike.race.R
import com.bike.race.databinding.ItemDriveWithMapBinding
import com.bike.race.uiModels.DriveItemWithMapData
import com.bike.race.utils.ConsoleLog
import com.bike.race.utils.DialogUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MapStyleOptions

class DriveItemWithMapViewHolder(
    itemView: View,
    private val onItemClick: (raceId: Long) -> Unit,
    private val onTagChange: (raceId: Long, tag: String) -> Unit,
    private val deleteRaceCallback: (raceId: Long) -> Unit
) : RecyclerView.ViewHolder(itemView), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var driveItemWithMapData: DriveItemWithMapData? = null
    private val viewBinding = ItemDriveWithMapBinding.bind(itemView)

    init {
        viewBinding.mapView.onCreate(null)
        viewBinding.mapView.getMapAsync(this)
    }

    fun renderData(driveItemWithMapData: DriveItemWithMapData) {
        viewBinding.mapView.tag = this
        this.driveItemWithMapData = driveItemWithMapData
        renderData()
        if (googleMap != null) {
            renderMap()
        }
    }

    private fun renderData() {
        val raceItemDataNonNull = driveItemWithMapData ?: return

        viewBinding.tagText.text = raceItemDataNonNull.getTagOrSourceDestText()
        viewBinding.timeText.text = raceItemDataNonNull.getTimeText()
        viewBinding.totalTimeText.text = raceItemDataNonNull.getTotalTime()
        viewBinding.distanceText.text = raceItemDataNonNull.getTotalDistance()
        viewBinding.topSpeedText.text = raceItemDataNonNull.getTopSpeed()

        viewBinding.tagText.setOnClickListener {
            DialogUtils.createInputDialog(
                context = viewBinding.root.context,
                input = raceItemDataNonNull.getTagText(),
                hint = viewBinding.root.context.getString(R.string.name_this_ride),
                positiveAction = viewBinding.root.context.getString(R.string.change),
                onSuccessAction = { tag ->
                    onTagChange(raceItemDataNonNull.getRaceId(), tag)
                }
            ).show()
        }

        viewBinding.deleteIcon.setOnClickListener {
            DialogUtils.createDialog(
                context = viewBinding.root.context,
                message = viewBinding.root.context.getString(R.string.delete_race_confirmation),
                positiveAction = viewBinding.root.context.getString(R.string.delete),
                negativeAction = viewBinding.root.context.getString(R.string.cancel),
                onSuccessAction = {
                    driveItemWithMapData?.getRaceId()?.let { raceId ->
                        deleteRaceCallback(raceId)
                    }
                },
                onNegativeAction = {}
            ).show()
        }

        viewBinding.root.setOnClickListener {
            onItemClick(raceItemDataNonNull.getRaceId())
        }
    }

    private fun renderMap() {

        val googleMapNonNull = googleMap ?: return
        val raceItemDataNonNull = driveItemWithMapData ?: return
        val latLngBounds = raceItemDataNonNull.getLatLngBounds()

        googleMapNonNull.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMapNonNull.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                viewBinding.root.context,
                R.raw.map_style_small
            )
        )

        try {
            googleMapNonNull.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 120))
            googleMapNonNull.moveCamera(CameraUpdateFactory.zoomOut())
        } catch (ignore: Exception) {
            ConsoleLog.e(e = ignore)
        }

        googleMapNonNull.addMarker(raceItemDataNonNull.getSourceMarker())
        googleMapNonNull.addMarker(raceItemDataNonNull.getDestMarker())
        googleMapNonNull.addPolyline(raceItemDataNonNull.getPolyLineOptions())

        googleMap?.setOnMapClickListener {
            viewBinding.root.performClick()
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        if (driveItemWithMapData != null) {
            renderMap()
        }
    }

    fun clearView() {
        googleMap?.clear()
        googleMap?.mapType = GoogleMap.MAP_TYPE_NONE
    }
}