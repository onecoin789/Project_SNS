package com.example.project_sns.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.project_sns.R

abstract class BaseFragment<T : ViewBinding>: Fragment() {

    private var _binding: T? = null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getFragmentBinding(inflater, container)
        return binding.root
    }

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): T

    fun backButton() {
        findNavController().popBackStack()
    }

    fun backToMain() {
        findNavController().popBackStack(R.id.mainFragment, false)
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
        _binding = null
    }
}