package com.bike.race.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bike.race.databinding.ViewEmptyBinding

class StateViewProvider {

    fun attachEmptyView(
        parent: ViewGroup,
        emptyMessage: String,
        actionText: String? = null,
        actionCallback: () -> Unit = {}
    ) {
        parent.removeAllViews()
        val viewBinding =
            ViewEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, true)
        viewBinding.emptyText.text = emptyMessage
        if (actionText?.isBlank() == false) {
            viewBinding.actionBtn.text = actionText
            viewBinding.actionBtn.visibility = View.VISIBLE
            viewBinding.actionBtn.setOnClickListener {
                actionCallback()
            }
        } else {
            viewBinding.actionBtn.visibility = View.GONE
        }
    }
}