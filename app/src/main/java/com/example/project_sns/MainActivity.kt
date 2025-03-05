package com.example.project_sns

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navigation
import com.example.project_sns.databinding.ActivityMainBinding
import com.example.project_sns.ui.BaseSnackBar
import com.example.project_sns.ui.view.login.LoginFragment
import com.example.project_sns.ui.view.main.MainSharedViewModel
import com.example.project_sns.ui.view.main.home.MainHomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_NOTIFICATION_PERMISSION = 1
        private const val TAG = "MainActivity"
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val mainSharedViewModel: MainSharedViewModel by viewModels()

    override fun onStart() {
        super.onStart()

        mainSharedViewModel.getLoginSession()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, 0)
            insets
        }


        // 알림 권한을 확인
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // [아직 권한이 허용되지 않은 경우] : 권한 요청 팝업 띄움
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATION_PERMISSION
                )
            }
        }

        window.apply {
            WindowInsetsControllerCompat(this, this.decorView).isAppearanceLightStatusBars = false
        }

        FcmUtil.initFcm(this)
        navigateFragment()


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 알림 권한이 허용된 경우
                BaseSnackBar.make(binding.root, "알림 권한이 허용되었습니다.").show()
            } else {
                // 알림 권한이 거부된 경우
                BaseSnackBar.make(binding.root, "알림 권한이 거부되었습니다.\n알림을 받으려면 설정에서 알림 권한을 허용하세요.")
                    .show()
            }
        }
    }

    private fun navigateFragment() {
        val targetFragment = intent.getStringExtra("chatRoomId")
        Log.d(TAG, "$targetFragment")
    }

}