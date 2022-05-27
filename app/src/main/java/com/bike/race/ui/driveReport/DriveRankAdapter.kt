package com.bike.race.ui.driveReport

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bike.race.R
import com.bike.race.customs.TextDrawable
import com.bike.race.databinding.ItemDriveRankBinding
import com.bike.race.uiModels.DriveRankItemData

class DriveRankAdapter(
    private val currentDriveId: Long,
    private val items: List<DriveRankItemData>,
    private val onCompare: (driveId: Long) -> Unit
) : RecyclerView.Adapter<DriveRankAdapter.DriveRankViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriveRankViewHolder {

        val viewBinding = ItemDriveRankBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return DriveRankViewHolder(itemView = viewBinding.root, onCompare = onCompare)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: DriveRankViewHolder, position: Int) {
        holder.render(currentDriveId, position + 1, items[position])
    }

    class DriveRankViewHolder(itemView: View, private val onCompare: (driveId: Long) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val viewBinding = ItemDriveRankBinding.bind(itemView)

        fun render(currentDriveId: Long, rank: Int, driveRankItemData: DriveRankItemData) {
            viewBinding.rootLayout.setOnClickListener {
                onCompare(driveRankItemData.id)
            }
            val defaultTextView = TextView(viewBinding.rootLayout.context).apply {
                setTextAppearance(R.style.TextAppearance_AppCompat_Body2)
            }
            viewBinding.rankText.setImageDrawable(
                TextDrawable.builder()
                    .textColor(defaultTextView.currentTextColor)
                    .buildRound(
                        rank.toString(),
                        viewBinding.rootLayout.context.getColor(R.color.contentBackground)
                    )
            )
            viewBinding.timeText.text = driveRankItemData.timeText()
            viewBinding.tagText.text = driveRankItemData.getTagText()
            viewBinding.happenedText.text = driveRankItemData.happenedText()

            if (currentDriveId == driveRankItemData.id) {
                viewBinding.compareIcon.visibility = View.GONE
            } else {
                viewBinding.compareIcon.visibility = View.VISIBLE
            }
        }
    }
}