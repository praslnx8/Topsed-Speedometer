package com.bike.race.ui.myAllDrive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bike.race.R
import com.bike.race.uiModels.DriveItemData

class MyAllDrivesAdapter(
    private val itemClickCallback: (driveId: Long) -> Unit,
    private val onTagChange: (driveId: Long, tag: String) -> Unit,
    private val deleteDriveCallback: (driveId: Long) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val driveItemList = mutableListOf<DriveItemData>()

    fun updateData(driveItemWithMapList: List<DriveItemData>) {
        this.driveItemList.clear()
        this.driveItemList.addAll(driveItemWithMapList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_drive, parent, false)
        return DriveItemViewHolder(view, itemClickCallback, onTagChange, deleteDriveCallback)
    }

    override fun getItemCount() = driveItemList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DriveItemViewHolder) {
            val driveItemData = driveItemList[position]
            holder.renderData(driveItemData)
        }
    }
}