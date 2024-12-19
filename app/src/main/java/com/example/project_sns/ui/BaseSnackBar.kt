package com.example.project_sns.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.example.project_sns.databinding.SnackbarBaseBinding
import com.google.android.material.snackbar.Snackbar

class BaseSnackBar(view: View, private val message: String) {

    companion object {
        fun make(view: View, message: String) = BaseSnackBar(view, message)
    }

    private val context = view.context
    private val snackBar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
    @SuppressLint("RestrictedApi")
    private val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout
    private val snackView = LayoutInflater.from(context)
    private val snackBarBinding = SnackbarBaseBinding.inflate(snackView, null, false)

    init {
        initView()
        initData()
    }

    private fun initView() {
        with(snackBarLayout) {
            removeAllViews()
            setPadding(0, 0, 0, 0)
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            addView(snackBarBinding.root, 0)
        }
    }

    private fun initData() {
        snackBarBinding.tvSnackBarMain.text = message
        snackBarBinding.tvSnackBarClose.setOnClickListener {
            snackBar.dismiss()
        }
    }

    fun show() {
        snackBar.show()
    }
}
