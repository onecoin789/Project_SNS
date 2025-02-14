package com.example.project_sns.ui.view.main.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentSettingBinding
import com.example.project_sns.ui.BaseDialog
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.view.main.MainSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    private val settingViewModel : SettingViewModel by viewModels()

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }



    private fun initView() {

        binding.ivSettingBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.clCancelAccount.setOnClickListener {
            findNavController().navigate(R.id.cancelAccountFragment)
        }

        binding.clSettingLogout.setOnClickListener {
            val dialog = BaseDialog("로그아웃", "로그아웃 하시겠습니까?")
            dialog.setButtonClickListener(object : BaseDialog.DialogClickEvent {
                override fun onClickConfirm() {
                    logout()
                }
            })
            dialog.show(childFragmentManager, "LogoutDialog")
        }
    }

    private fun logout() {
        viewLifecycleOwner.lifecycleScope.launch {
            settingViewModel.logout()
//            mainSharedViewModel.logoutData()
            mainSharedViewModel.checkLogin(false)
            backButton()
            findNavController().navigate(R.id.loginFragment)
        }
    }
}