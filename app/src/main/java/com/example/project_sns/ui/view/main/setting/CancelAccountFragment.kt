package com.example.project_sns.ui.view.main.setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentCancelAccountBinding
import com.example.project_sns.ui.BaseDialog
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.BaseSnackBar
import com.example.project_sns.ui.CurrentUser
import com.example.project_sns.ui.util.sendToast
import com.example.project_sns.ui.util.visibleBottomBar
import com.example.project_sns.ui.view.main.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CancelAccountFragment : BaseFragment<FragmentCancelAccountBinding>() {

    companion object {
        private const val TAG = "CancelAccountFragment"
    }

    private val settingViewModel: SettingViewModel by viewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCancelAccountBinding {
        return FragmentCancelAccountBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visibleBottomBar(binding.clCancelAccount)

        initView()
        collectCancelAccountResult()

    }

    private fun initView() {

        val currentUserUid = CurrentUser.userData?.uid.toString()
        val currentUserEmail = CurrentUser.userData?.email

        binding.etCancelAccountMail.hint = currentUserEmail

        binding.ivCancelAccountBack.setOnClickListener {
            backButton()
        }

        binding.btnCancelAccountConfirm.setOnClickListener {
            val email = binding.etCancelAccountMail.text.toString()
            val intentEmail = binding.etCancelAccountMail.text.toString()
            if (currentUserEmail == email) {
                settingViewModel.cancelAccount(currentUserUid, currentUserEmail)
                Log.d(TAG, "same : $currentUserUid, $currentUserEmail, $intentEmail")
            } else {
                sendToast(requireContext(), "이메일이 일치하지 않습니다.")
                Log.d(TAG, "difference : $currentUserUid, $currentUserEmail, $intentEmail")
            }
        }

//        binding.btnCancelAccountConfirm.setOnClickListener {
//            Log.d(TAG, "$currentUserEmail, $email")
//            if (currentUserEmail == email) {
//                val dialog = BaseDialog("회원탈퇴", "이메일이 일치합니다.\n회원탈퇴 하시겠습니까?")
//                dialog.setButtonClickListener(object : BaseDialog.DialogClickEvent {
//                    override fun onClickConfirm() {
//                        settingViewModel.cancelAccount(currentUserUid, currentUserEmail)
//                    }
//                })
//                dialog.show(childFragmentManager, "CancelAccountDialog")
//            } else {
//                BaseSnackBar.make(binding.root, "이메일이 일치하지 않습니다.").show()
//            }
//        }
    }

    private fun collectCancelAccountResult() {
        settingViewModel.cancelAccountResult.observe(viewLifecycleOwner) { result ->
            if (result == true) {
                logout()
                findNavController().navigate(R.id.loginFragment)
            } else {
                BaseSnackBar.make(binding.root, "회원탈퇴에 실패하였습니다.").show()
            }
        }
    }

    private fun logout() {
        viewLifecycleOwner.lifecycleScope.launch {
            settingViewModel.logout()
//            mainSharedViewModel.logoutData()
            mainSharedViewModel.checkLogin(false)
        }
    }


}