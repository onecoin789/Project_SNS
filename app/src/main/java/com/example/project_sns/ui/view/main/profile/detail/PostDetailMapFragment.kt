package com.example.project_sns.ui.view.main.profile.detail

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentPostDetailMapBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.og_tag.OgTagParser
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.launch


class PostDetailMapFragment : BaseFragment<FragmentPostDetailMapBinding>(), OnMapReadyCallback {

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private lateinit var marker: Marker


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
        bottomBehavior.isHideable = false

        lifecycleScope.launch {
            mainSharedViewModel.postData.observe(viewLifecycleOwner) { postData ->
                val mapData = postData?.mapData
                if (mapData != null) {
                    val placeName = mapData.placeName
                    val placeUrl = mapData.placeUrl
                    val addressName = mapData.addressName
                    if (placeName != null && addressName != null && placeUrl != null) {
                        val ogTags = OgTagParser().getContents(placeUrl)
                        ogTags?.let { content ->
                            val title = content.ogTitle
                            val description = content.ogDescription
                            val url = content.ogUrl
                            val image = content.image

                            Glide.with(requireContext()).load(image).into(binding.ivMapOGImage)
                            binding.tvMapOGTitle.text = title
                            binding.tvMapOGDescription.text = description
                            binding.tvMapOGUrl.text = url
                        }
                        binding.tvMapPlaceName.text = placeName
                        binding.tvMapAddressName.text = addressName
                        binding.tvMapPlaceUrl.text = placeUrl

                        binding.tvMapPlaceUrl.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(placeUrl))
                            requireActivity().startActivity(intent)
                        }
                    }
                }
            }
        }

        //clickEvent
        binding.ivMapBack.setOnClickListener {
            backButton()
            marker.map = null
        }
    }

    private fun initMapView() {
        val fm = childFragmentManager
        val mapFragment =
            fm.findFragmentById(R.id.fcMap) as MapFragment? ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fcMap, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {

        lifecycleScope.launch {
            mainSharedViewModel.postData.observe(viewLifecycleOwner) { postData ->
                val mapData = postData?.mapData
                if (mapData != null) {
                    val latData = mapData.lat
                    val lngData = mapData.lng
                    val placeName = mapData.placeName
                    if (latData != null && lngData != null && placeName != null) {

                        val cameraUpdate =
                            CameraUpdate.scrollAndZoomTo(LatLng(latData, lngData), 18.0)


                        naverMap.moveCamera(cameraUpdate)
                        naverMap.mapType = NaverMap.MapType.Navi
                        naverMap.isNightModeEnabled = true

                        marker = Marker()
                        marker.position = LatLng(latData, lngData)
                        marker.map = naverMap
                        marker.icon = MarkerIcons.BLACK
                        marker.iconTintColor = Color.RED
                        marker.captionText = placeName

                        Log.d("Tag_Location", "$latData, $lngData")
                    }
                }
            }
        }
    }
}


