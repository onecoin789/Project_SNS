package com.example.project_sns.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.project_sns.databinding.FragmentDialogBinding


class BaseDialog(private val mainText: String, private val subText: String): DialogFragment() {

    private lateinit var buttonClickListener: DialogClickEvent

    interface DialogClickEvent {
        fun onClickConfirm()
    }

    private var _binding: FragmentDialogBinding? = null
    val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
            buttonClickListener.onClickConfirm()
            dismiss()
        }

        binding.btnDlCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setButtonClickListener(buttonClickListener: DialogClickEvent) {
        this.buttonClickListener = buttonClickListener
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}