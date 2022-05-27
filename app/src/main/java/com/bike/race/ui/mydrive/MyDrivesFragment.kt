package com.bike.race.ui.mydrive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bike.race.R
import com.bike.race.databinding.FragmentMyDrivesBinding
import com.bike.race.ui.FragmentHostActivity
import com.bike.race.ui.driveReport.DriveReportActivity
import com.bike.race.utils.StateViewProvider
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyDrivesFragment : Fragment() {

    private val viewModel: MyDrivesViewModel by viewModel()
    private val stateViewProvider: StateViewProvider = get()
    private lateinit var viewBinding: FragmentMyDrivesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentMyDrivesBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MyDrivesAdapter(::onItemClick, ::changeTag, ::deleteDrive, ::viewAll)
        viewBinding.recyclerView.apply {
            this.setHasFixedSize(true)
            this.adapter = adapter
            setRecyclerListener {
                (it as? DriveItemWithMapViewHolder)?.clearView()
            }
        }

        viewModel.getMyDrives().observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                viewBinding.recyclerView.visibility = View.GONE
                viewBinding.driveStatsLayout.visibility = View.GONE
                viewBinding.stateLayout.visibility = View.VISIBLE
                stateViewProvider.attachEmptyView(
                    viewBinding.stateLayout,
                    getString(R.string.not_yet_started_the_game)
                )
            } else {
                viewBinding.stateLayout.visibility = View.GONE
                viewBinding.recyclerView.visibility = View.VISIBLE
                viewBinding.driveStatsLayout.visibility = View.VISIBLE
                adapter.set(it)
            }
        }

        viewModel.getMyDriveStats().observe(viewLifecycleOwner) {
            viewBinding.topSpeedText.text = it.getTopSpeed()
            viewBinding.totalDriveText.text = it.getTotalDrives()
        }

    }

    private fun changeTag(driveId: Long, tag: String) {
        viewModel.changeTag(driveId, tag)
    }

    private fun deleteDrive(driveId: Long) {
        viewModel.deleteDrive(driveId)
    }

    private fun onItemClick(driveId: Long) {
        activity?.let {
            DriveReportActivity.open(it, driveId)
        }
    }

    private fun viewAll() {
        activity?.let {
            FragmentHostActivity.open(it, FragmentHostActivity.FRAG_HOST_MY_ALL_DRIVES)
        }
    }
}