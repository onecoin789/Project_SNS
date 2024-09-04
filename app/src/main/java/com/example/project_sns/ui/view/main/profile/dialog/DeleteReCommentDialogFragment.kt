package com.example.project_sns.ui.view.main.profile.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.project_sns.databinding.FragmentDialogBinding
import com.example.project_sns.ui.CurrentPost
import com.example.project_sns.ui.view.main.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeleteReCommentDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!

    private var mainText: String? = null
    private var subText: String? = null

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mainText = it.getString("mainText")
            subText = it.getString("subText")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDlMain.text = mainText
        binding.tvDlSub.text = subText

        binding.btnDlConfirm.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                mainSharedViewModel.postData.observe(viewLifecycleOwner) { currentPostData ->
                    mainSharedViewModel.selectedCommentData.observe(viewLifecycleOwner) { currentCommentData ->
                        mainSharedViewModel.selectedReCommentData.observe(viewLifecycleOwner) { item ->
                            if (currentPostData != null && currentCommentData != null && item != null) {
                                mainSharedViewModel.deleteReComment(
                                    currentPostData.postId,
                                    currentCommentData.commentId,
                                    item.commentId
                                )
                            }
                        }
                    }
                }
            }
            dismiss()
        }

        binding.btnDlCancel.setOnClickListener {
            dismiss()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}