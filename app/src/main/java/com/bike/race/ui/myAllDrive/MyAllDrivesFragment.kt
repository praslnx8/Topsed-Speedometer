package com.bike.race.ui.myAllDrive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bike.race.databinding.FragmentMyAllDrivesBinding
import com.bike.race.ui.driveReport.DriveReportActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyAllDrivesFragment : Fragment() {

    private val viewModel: MyAllDrivesViewModel by viewModel()

    private lateinit var adapter: MyAllDrivesAdapter
    private lateinit var viewBinding: FragmentMyAllDrivesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentMyAllDrivesBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MyAllDrivesAdapter(::itemClick, ::changeTag, ::deleteDrive)
        viewBinding.recyclerView.adapter = adapter

        viewModel.getMyDrivesLiveData().observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }

    private fun changeTag(driveId: Long, tag: String) {
        viewModel.changeTag(driveId, tag)
    }

    private fun deleteDrive(driveId: Long) {
        viewModel.deleteDrive(driveId)
    }

    private fun itemClick(driveId: Long) {
        DriveReportActivity.open(requireActivity(), driveId)
    }
}
