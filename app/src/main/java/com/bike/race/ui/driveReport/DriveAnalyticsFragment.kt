package com.bike.race.ui.driveReport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bike.race.R
import com.bike.race.databinding.FragmentDriveAnalyticsBinding
import com.bike.race.uiModels.DriveAnalyticsData
import com.bike.race.utils.DialogUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*


class DriveAnalyticsFragment : Fragment() {

    private val viewModel: DriveReportViewModel by sharedViewModel()
    private lateinit var viewBinding: FragmentDriveAnalyticsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentDriveAnalyticsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getDriveAnalyticsLiveData().observe(viewLifecycleOwner) {
            renderAnalyticsReportData(it)
        }
    }

    private fun renderAnalyticsReportData(driveAnalyticsData: DriveAnalyticsData) {

        viewBinding.tagText.text = driveAnalyticsData.getTagText()
        if (driveAnalyticsData.getDistance() == 0) {
            viewBinding.tagText.visibility = View.GONE
        }
        viewBinding.totalTimeText.text = driveAnalyticsData.getTotalTimeAsString()
        viewBinding.totalDistanceText.text = driveAnalyticsData.getTotalDistanceAsString()

        viewBinding.startLocalityText.text = driveAnalyticsData.getStartLocality()
        viewBinding.endLocalityText.text = driveAnalyticsData.getEndLocality()

        viewBinding.startTimeText.text = driveAnalyticsData.getStartTime()
        viewBinding.endTimeText.text = driveAnalyticsData.getEndTime()

        viewBinding.idleTimeText.text = driveAnalyticsData.getIdleTime()
        viewBinding.avgSpeedText.text = driveAnalyticsData.getAverageSpeed()
        viewBinding.topSpeedText.text = driveAnalyticsData.getTopSpeed()
        viewBinding.noOfStopsText.text = driveAnalyticsData.getNoOfStops()

        viewBinding.pauseTimeText.text = driveAnalyticsData.getPauseTimeAsString()

        driveAnalyticsData.getLineData()?.let {
            renderSpeedChart(it)
        }

        viewBinding.tagText.setOnClickListener {
            DialogUtils.createInputDialog(
                context = viewBinding.root.context,
                input = driveAnalyticsData.getTagText(),
                hint = viewBinding.root.context.getString(R.string.name_this_ride),
                positiveAction = viewBinding.root.context.getString(R.string.change),
                onSuccessAction = { tag ->
                    viewModel.changeTag(tag)
                }
            ).show()
        }
    }

    private fun renderSpeedChart(lineData: LineData) {
        viewBinding.speedChart.data = lineData
        viewBinding.speedChart.apply {
            setTouchEnabled(false)
            setPinchZoom(false)
            description.isEnabled = true
            description.textColor = requireContext().getColor(R.color.secondaryText)
            description.text = getString(R.string.top_speed)
        }

        viewBinding.speedChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            textColor = requireContext().getColor(R.color.secondaryText)
        }

        viewBinding.speedChart.axisRight.apply {
            textColor = requireContext().getColor(R.color.secondaryText)
        }

        viewBinding.speedChart.axisLeft.apply {
            textColor = requireContext().getColor(R.color.secondaryText)
        }

        viewBinding.speedChart.xAxis.valueFormatter = object : IndexAxisValueFormatter() {

            private val mFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)

            override fun getFormattedValue(value: Float): String {
                return mFormat.format(Date(value.toLong()))
            }
        }
        viewBinding.speedChart.invalidate()
        viewBinding.speedChart.refreshDrawableState()
    }
}