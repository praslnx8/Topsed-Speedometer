package com.bike.race.ui.compare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bike.race.R
import com.bike.race.databinding.FragmentCompareDriveBinding
import com.bike.race.uiModels.CompareData

class CompareDataFragment : Fragment() {

    private val viewModel: CompareViewModel by activityViewModels()
    private lateinit var viewBinding: FragmentCompareDriveBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentCompareDriveBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.compareLiveData.observe(viewLifecycleOwner, { result ->
            showData(result)
        })
    }

    private fun showData(compareData: CompareData) {
        val currentDriveData = compareData.comparableDriveData1
        val compareDriveData = compareData.comparableDriveData2

        viewBinding.myTimeText.text = currentDriveData.getTime()
        viewBinding.myAvgSpeedText.text = currentDriveData.getAvgSpeed()
        viewBinding.myTopSpeedText.text = currentDriveData.getTopSpeed()
        viewBinding.myTotalDistanceText.text = currentDriveData.getTotalDistance()

        if (compareDriveData != null) {
            viewBinding.otherTimeText.text = compareDriveData.getTime()
            viewBinding.otherAvgSpeedText.text = compareDriveData.getAvgSpeed()
            viewBinding.otherTopSpeedText.text = compareDriveData.getTopSpeed()
            viewBinding.otherTotalDistanceText.text = compareDriveData.getTotalDistance()

            if (currentDriveData.getTopSpeed() < compareDriveData.getTopSpeed()) {
                setWinner(viewBinding.otherTopSpeedText)
            } else if (currentDriveData.getTopSpeed() > compareDriveData.getTopSpeed()) {
                setWinner(viewBinding.myTopSpeedText)
            }

            if (currentDriveData.getAvgSpeed() < compareDriveData.getAvgSpeed()) {
                setWinner(viewBinding.otherAvgSpeedText)
            } else if (currentDriveData.getAvgSpeed() > compareDriveData.getAvgSpeed()) {
                setWinner(viewBinding.myAvgSpeedText)
            }

            if (currentDriveData.getTotalDistance() < compareDriveData.getTotalDistance()) {
                setWinner(viewBinding.myTotalDistanceText)
            } else if (currentDriveData.getTime() > compareDriveData.getTime()) {
                setWinner(viewBinding.otherTotalDistanceText)
            }

        } else {
            viewBinding.otherTimeText.text = "--"
            viewBinding.otherAvgSpeedText.text = "--"
            viewBinding.otherTopSpeedText.text = "--"
            viewBinding.otherTotalDistanceText.text = "--"
        }
    }

    private fun setWinner(textView: TextView) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_baseline_flash_on_24,
            0,
            0,
            0
        )
    }
}