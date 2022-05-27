package com.bike.race.ui.myAllDrive

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bike.race.R
import com.bike.race.databinding.ItemDriveBinding
import com.bike.race.uiModels.DriveItemData
import com.bike.race.utils.DialogUtils

class DriveItemViewHolder(
    itemView: View,
    private val itemClickCallback: (driveId: Long) -> Unit,
    private val onTagChange: (raceId: Long, tag: String) -> Unit,
    private val deleteRaceCallback: (driveId: Long) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private var driveItemWithMapData: DriveItemData? = null

    fun renderData(driveItemWithMapData: DriveItemData) {
        this.driveItemWithMapData = driveItemWithMapData
        renderData()
    }

    private fun renderData() {
        val driveItemDataNonNull = driveItemWithMapData ?: return

        val viewBinding = ItemDriveBinding.bind(itemView)

        viewBinding.tagText.text = driveItemDataNonNull.getTagOrSourceDestText()
        viewBinding.timeText.text = driveItemDataNonNull.getTimeText()
        viewBinding.topSpeedText.text = driveItemDataNonNull.getTopSpeed()
        viewBinding.totalTimeText.text = driveItemDataNonNull.getTotalTime()
        viewBinding.distanceText.text = driveItemDataNonNull.getTotalDistance()

        viewBinding.tagText.setOnClickListener {
            DialogUtils.createInputDialog(
                context = viewBinding.root.context,
                input = driveItemDataNonNull.getTagText(),
                hint = viewBinding.root.context.getString(R.string.name_this_ride),
                positiveAction = viewBinding.root.context.getString(R.string.change),
                onSuccessAction = { tag ->
                    onTagChange(driveItemDataNonNull.getDriveId(), tag)
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
                    driveItemWithMapData?.getDriveId()?.let { raceId ->
                        deleteRaceCallback(raceId)
                    }
                },
                onNegativeAction = {}
            ).show()
        }

        viewBinding.mainLayout.setOnClickListener {
            itemClickCallback(driveItemDataNonNull.getDriveId())
        }
    }
}