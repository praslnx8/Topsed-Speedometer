package com.bike.race.ui.mydrive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bike.race.R
import com.bike.race.customs.dynamicAdapter.DynamicAdapter
import com.bike.race.customs.dynamicAdapter.DynamicContent
import com.bike.race.databinding.ItemDriveWithMapBinding
import com.bike.race.uiModels.DriveItemCollection
import com.bike.race.uiModels.DriveItemWithMapData

class MyDrivesAdapter(
    private val onItemClick: (driveId: Long) -> Unit,
    private val onTagChange: (driveId: Long, tag: String) -> Unit,
    private val deleteDriveCallback: (driveId: Long) -> Unit,
    private val viewAllCallback: () -> Unit
) : DynamicAdapter() {

    private val itemTypeDrive = 1
    private val itemTypeViewAll = 2

    fun set(driveItemCollection: DriveItemCollection) {
        val dynamicList = driveItemCollection.driveItemWithMapList.map {
            DynamicContent(it, itemTypeDrive)
        }.toMutableList()
        if (dynamicList.size >= driveItemCollection.maxFetched) {
            dynamicList.add(DynamicContent("View More", itemTypeViewAll))
        }

        updateData(dynamicList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemTypeDrive -> {
                val viewBinding = ItemDriveWithMapBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                DriveItemWithMapViewHolder(
                    viewBinding.root,
                    onItemClick,
                    onTagChange,
                    deleteDriveCallback
                )
            }
            else -> {
                val view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_view_all, parent, false)
                ViewAllHolder(view, viewAllCallback)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val content = getData(position).content
        if (holder is DriveItemWithMapViewHolder && content is DriveItemWithMapData) {
            holder.renderData(content)
        }
    }
}