package com.example.project_sns.ui.view.main.profile.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentPostDetailMapBinding
import com.example.project_sns.ui.BaseBottomSheet
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import kotlinx.coroutines.launch
import kotlin.math.ln


class PostDetailMapFragment : BaseFragment<FragmentPostDetailMapBinding>(), OnMapReadyCallback {

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPostDetailMapBinding {
        return FragmentPostDetailMapBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMapView()
        initView()

    }

    private fun initView() {

        val bottomBehavior = BottomSheetBehavior.from(binding.clMapBottomSheet)
        bottomBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomBehavior.peekHeight = 50
        bottomBehavior.isHideable = false


        //clickEvent
        binding.ivMapBack.setOnClickListener {
            backButton()
        }
    }

    private fun initMapView() {
        val fm = parentFragmentManager
        val mapFragment =
            fm.findFragmentById(R.id.fcMap) as MapFragment? ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fcMap, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {

        lifecycleScope.launch {
            mainSharedViewModel.postData.observe(viewLifecycleOwner) { postData ->
                val mapData = postData?.mapData
                if (mapData != null) {
                    val latData = mapData.lat
                    val lngData = mapData.lng
                    if (latData != null && lngData != null) {

                        val options = NaverMapOptions()
                            .camera(CameraPosition(LatLng(latData, lngData), 10.0))
                            .mapType(NaverMap.MapType.Navi)
                            .enabledLayerGroups(NaverMap.LAYER_GROUP_BUILDING)
                            .nightModeEnabled(true)

                        MapFragment.newInstance(options)

                        val marker = Marker()
                        marker.position = LatLng(latData, lngData)
                        marker.map = naverMap

                        Log.d("Tag_Location", "$latData, $lngData")
                    }
                }
            }
        }
    }
}


