package com.bike.race.ui.driveReport

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bike.race.databinding.FragmentDriveRankBinding
import com.bike.race.ui.compare.CompareActivity
import com.bike.race.uiModels.DriveRankData
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DriveRankFragment : Fragment() {

    private val viewModel: DriveReportViewModel by sharedViewModel()
    private lateinit var viewBinding: FragmentDriveRankBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentDriveRankBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getDriveRankLiveData().observe(viewLifecycleOwner) {
            renderDriveRankData(it)
        }
    }

    private fun renderDriveRankData(driveRankData: DriveRankData) {
        viewBinding.progressLayout.visibility = View.GONE
        viewBinding.nonEligibleLayout.visibility = View.GONE
        viewBinding.driveLocalityText.text = driveRankData.getLocality()

        when {
            driveRankData.fetching -> {
                viewBinding.progressLayout.visibility = View.VISIBLE
            }
            driveRankData.noItems -> {
                viewBinding.nonEligibleLayout.visibility = View.VISIBLE
            }
            else -> {
                viewBinding.recyclerView.adapter =
                    DriveRankAdapter(
                        viewModel.getCurrentDriveId(),
                        driveRankData.items
                    ) { driveIdToCompare ->
                        CompareActivity.open(
                            context = requireContext(),
                            currentDriveId = viewModel.getCurrentDriveId(),
                            compareDriveId = driveIdToCompare
                        )
                    }
            }
        }
    }


}