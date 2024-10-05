package com.example.project_sns.ui.view.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentMyProfileSearchMapBinding
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.model.KakaoDocumentsModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyProfileSearchMapFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentMyProfileSearchMapBinding? = null
    private val binding get() = _binding!!

    private val mainSharedViewModel: MainSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyProfileSearchMapBinding.inflate(inflater, container, false)


        initView()

        return binding.root
    }

    private fun initView() {
        binding.btnSearchMap.setOnClickListener {
          initRv()
        }
    }


    private fun initRv() {
        val query = binding.etSearchMap.text.toString()
        val mapAdapter = KakaoMapListAdapter { data ->
            sendMapData(data)
            findNavController().popBackStack()
        }
        with(binding.rvSearchMap) {
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = mapAdapter
        }
        if (query.isEmpty()) {
            Toast.makeText(requireContext(), "검색어를 입력해주세요!", Toast.LENGTH_SHORT).show()
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                mainSharedViewModel.searchMapList(query = query)
                mainSharedViewModel.mapList.collect { list ->
                    if (list.isNotEmpty()) {
                        binding.rvSearchMap.visibility = View.VISIBLE
                        binding.tvSearchMap.visibility = View.GONE
                        mapAdapter.submitList(list)
                    } else {
                        binding.tvSearchMap.visibility = View.VISIBLE
                        binding.rvSearchMap.visibility = View.GONE
                    }
                }
            }
        }

    }

    private fun sendMapData(mapData: KakaoDocumentsModel) {
        val bundle = Bundle()
        bundle.putString("placeName", mapData.placeName)
        bundle.putString("addressName", mapData.addressName)
        bundle.putString("lat", mapData.lat)
        bundle.putString("lng", mapData.lng)
        bundle.putString("placeUrl", mapData.placeUrl)
        setFragmentResult("data", bundleOf("mapData" to bundle))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}