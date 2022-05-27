package com.bike.race.ui.mydrive

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bike.race.databinding.ItemViewAllBinding

class ViewAllHolder(itemView: View, viewAllCallback: () -> Unit) :
    RecyclerView.ViewHolder(itemView) {

    init {
        ItemViewAllBinding.bind(itemView).viewAllBtn.setOnClickListener {
            viewAllCallback()
        }
    }
}