package com.example.project_sns.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.project_sns.R
import com.example.project_sns.ui.model.PostDataModel
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheet<T : ViewBinding>:BottomSheetDialogFragment() {

    private val mainSharedViewModel : MainSharedViewModel by activityViewModels()

    private var _binding: T? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getFragmentBinding(inflater, container)
        return binding.root
    }

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): T

    fun inflateDialog(mainText: String, subText: String) {
        val bundle = Bundle()
        bundle.putString("mainText", mainText)
        bundle.putString("subText", subText)
        findNavController().navigate(R.id.deleteDialogFragment, bundle)
    }

    fun inflateCommentDialog(mainText: String, subText: String) {
        val bundle = Bundle()
        bundle.putString("mainText", mainText)
        bundle.putString("subText", subText)
        findNavController().navigate(R.id.deleteCommentDialogFragment, bundle)
    }

    fun inflateReCommentDialog(mainText: String, subText: String) {
        val bundle = Bundle()
        bundle.putString("mainText", mainText)
        bundle.putString("subText", subText)
        findNavController().navigate(R.id.deleteReCommentDialogFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainSharedViewModel.clearCommentData()
        _binding = null
    }
}