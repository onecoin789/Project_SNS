package com.example.project_sns.ui.view.login

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.project_sns.R
import com.example.project_sns.databinding.FragmentLoginBinding
import com.example.project_sns.ui.BaseFragment
import com.example.project_sns.ui.util.CheckLogin
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.TimeZone


@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {


    private val loginViewModel: LoginViewModel by viewModels()


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    private fun initView() {

        binding.tvLoginSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.btnLogin.setOnClickListener {
            initLogin()
            collectFlow()
        }

        binding.ivLoginShowPassword.setOnClickListener {
            binding.ivLoginHidePassword.visibility = View.GONE
            showPassword()
        }

        binding.ivLoginKakao.setOnClickListener {
            kakaoLogin()
            collectKakaoFlow()
        }
    }

    private fun collectFlow() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                loginViewModel.loginEvent.collect { checkLogin ->
                    when (checkLogin) {
                        is CheckLogin.LoginSuccess -> {
                            Toast.makeText(requireContext(), checkLogin.message, Toast.LENGTH_SHORT)
                                .show()
                            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                        }

                        is CheckLogin.LoginFail -> {
                            Toast.makeText(requireContext(), checkLogin.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun collectKakaoFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                loginViewModel.kakaoLoginEvent.collect { checkLogin ->
                    when (checkLogin) {
                        is CheckLogin.LoginSuccess -> {
                            Toast.makeText(requireContext(), checkLogin.message, Toast.LENGTH_SHORT)
                                .show()
                            Thread.sleep(3000)
                            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                        }

                        is CheckLogin.LoginFail -> {
                            Toast.makeText(requireContext(), checkLogin.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun initLogin() {
        val email = binding.etLoginEmail.text.toString()
        val password = binding.etLoginPassword.text.toString()

        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.login(email, password)
        }
    }

    private fun showPassword() {
        val showPw = binding.ivLoginShowPassword
        when (showPw.tag) {
            "0" -> {
                binding.ivLoginShowPassword.tag = "1"
                binding.etLoginPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.ivLoginShowPassword.setImageResource(R.drawable.ic_show)
            }

            "1" -> {
                binding.ivLoginShowPassword.tag = "0"
                binding.etLoginPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.ivLoginShowPassword.setImageResource(R.drawable.ic_hide)
            }
        }
    }

    private fun kakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.d("kakao_fail", "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    loginViewModel.kakaoLogin(token.accessToken)
                }
                Log.d("kakao_success", "카카오계정으로 로그인 성공 ${token.accessToken}")
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Log.d("kakao_fail", "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(
                        requireContext(),
                        callback = callback
                    )
                } else if (token != null) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        loginViewModel.kakaoLogin(token.accessToken)
                    }
                    Log.d("kakao_success", "카카오톡으로 로그인 성공 ${token.accessToken}")
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
        }
    }

//    private fun getCustomToken(accessToken: String) {
//
//        val function = FirebaseFunctions.getInstance("asia-northeast3")
//        val data = hashMapOf(
//            "token" to accessToken
//        )
//
//        function
//            .getHttpsCallable("kakaoCustomAuth")
//            .call(data)
//            .addOnCompleteListener { task ->
//                // 호출 성공
//                val result = task.result?.data as HashMap<*, *>
//                var mKey: String? = null
//                for (key in result.keys) {
//                    mKey = key.toString()
//                }
//                val customToken = result[mKey].toString()
//                firebaseAuthWithKakao(customToken)
//                UserApiClient.instance.me { user, error ->
//                    if (error != null) {
//                        Log.d("user_info", "${error}")
//                    }
//                    else if (user != null) {
//                        Log.d("user_info", "사용자 정보 요청 성공" +
//                                "\n회원번호: kakao:${user.id}" +
//                                "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
//                                "\n프로필사진: ${user.kakaoAccount?.profile?.profileImageUrl}")
//                    }
//                }
//                Log.d("kakao_success_1", customToken)
//
//
//            }
//    }
//
//    private fun firebaseAuthWithKakao(customToken: String) {
//        val auth = FirebaseAuth.getInstance()
//        auth.signInWithCustomToken(customToken).addOnCompleteListener { result ->
//            if (result.isSuccessful) {
//                Log.d("kakao_success_2", "카카오톡으로 로그인 성공")
//                val uid = auth.currentUser?.uid
//                Log.d("kakao_success_uid", "${uid}")
//            } else {
//                Log.d("kakao_fail_2", "카카오톡으로 로그인 실패")
//            }
//        }
//    }
}
