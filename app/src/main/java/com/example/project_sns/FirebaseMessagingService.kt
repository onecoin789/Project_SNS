package com.example.project_sns

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

//class FirebaseMessagingService : FirebaseMessagingService() {
//
//    companion object {
//        private const val TAG = "FirebaseMessagingService"
//    }
//    private var FCMToken = ""
//
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//        Log.d(TAG, "new Token ${token}")
//
//        val pref = this.getSharedPreferences("firebaseToken", Context.MODE_PRIVATE)
//        val editor = pref.edit()
//        editor.putString("firebaseToken", token).apply()
//        editor.apply()
//        Log.i(TAG, "토큰 저장 완료")
//        sendRegistrationToServer(token)
//        //여기서 서버에 토큰 저장시키기Todo
//    }
//
//    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//        Log.d(TAG, "From : ${message.from}")
//
//        //받은 remoteMessage의 값 출력해보기. 데이터메세지 / 알림메세지
//        Log.d(TAG, "Message data : ${message.data}")
//        Log.d(TAG, "Message notification : ${message.notification?.body!!}")
//
//        /*if (message.notification?.body!!.isNotEmpty()) {
//            setNotification(message)
//            if (true) {
//
//            }
//        } else {
//            Log.e(TAG, "data가 비어있습니다. 메시지를 수신하지 못했습니다.")
//            //메세지에 전송된 알림 데이터가 포함되어있는지 확인하기위함
//            setNotification(
//                message
//            )
//        }*/
//
//        if (message.data.isNotEmpty()) {
//            scheduleJob()
//        } else {
//            handleNow()
//        }
//
//        //알람 내용이 비어 있지 않은 경우
//        if (message.notification!= null) {
//            setNotification(message)
//        }
//
//    }
//
//    // 메시지에 데이터 페이로드가 포함 되어 있을 때 실행되는 메서드
//    // 장시간 실행 (10초 이상) 작업의 경우 WorkManager를 사용하여 비동기 작업을 예약한다.
//    private fun scheduleJob() {
//        val work = OneTimeWorkRequest.Builder(MyWorker::class.java)
//            .build()
//        WorkManager.getInstance(this)
//            .beginWith(work)
//            .enqueue()
//    }
//
//    // 메시지에 데이터 페이로드가 포함 되어 있을 때 실행되는 메서드
//    // 10초 이내로 걸릴 때 메시지를 처리한다.
//    private fun handleNow() {
//        Log.d(TAG, "Short lived task is done.")
//    }
//
//    // 타사 서버에 토큰을 유지해주는 메서드이다.
//    private fun sendRegistrationToServer(token: String?) {
//        Log.d(TAG, "sendRegistrationTokenToServer($token)")
//    }
//
//    private fun setNotification(message : RemoteMessage) {
//        val channelId = "fcm_set_notification_channel" // 알림 채널 이름
//        val channelName = "fcm_set_notification"
//        val channelDescription = "fcm_send_notify"
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // 오레오 버전 이후에는 채널이 필요
//        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
//            val importance = NotificationManager.IMPORTANCE_HIGH// 중요도 (HIGH: 상단바 표시 가능)
//            val channel = NotificationChannel(channelId, channelName, importance).apply{
//                description= channelDescription
//            }
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        // RequestCode, Id를 고유값으로 지정하여 알림이 개별 표시
//        val uniId: Int = (System.currentTimeMillis() / 7).toInt()
//
//        // 일회용 PendingIntent : Intent 의 실행 권한을 외부의 어플리케이션에게 위임
//        val intent = Intent(this, MainActivity::class.java)
//        /*//각 key, value 추가
//        for(key in message.data.keys){
//            intent.putExtra(key, message.data.getValue(key))
//        }*/
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Activity Stack 을 경로만 남김(A-B-C-D-B => A-B)
//
//        //PendingIntent.FLAG_MUTABLE은 PendingIntent의 내용을 변경할 수 있도록 허용, PendingIntent.FLAG_IMMUTABLE은 PendingIntent의 내용을 변경할 수 없음
//        val pendingIntent = if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.S) {
//            PendingIntent.getActivity(this, uniId, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE)
//        } else {
//            PendingIntent.getActivity(this, uniId, intent, PendingIntent.FLAG_IMMUTABLE)
//        }
//
//        val title: String? = message.notification?.title
//        val body: String? = message.notification?.body
//
//// 알림에 대한 UI 정보, 작업
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setPriority(NotificationCompat.PRIORITY_HIGH) // 중요도 (HIGH: 상단바 표시 가능)
//            .setSmallIcon(R.mipmap.ic_launcher) // 아이콘 설정
//            .setContentTitle(title) // 제목
//            .setContentText(body) // 메시지 내용
//            .setAutoCancel(true) // 알람클릭시 삭제여부
//            .setContentIntent(pendingIntent) // 알림 실행 시 Intent
//
//        notificationManager.notify(uniId, notificationBuilder.build())
//    }
//
//    //필요한 곳 토큰 가져오기
//    fun getFirebaseToken() : String {
//        //비동기 방식
//        FirebaseMessaging.getInstance().token.addOnSuccessListener{
//            Log.d(TAG, "token=${it}")
//            FCMToken =it
//        }
//        return FCMToken
//    }
//
//    internal class MyWorker(appContext: Context, workerParams: WorkerParameters) :
//        Worker(appContext, workerParams) {
//        override fun doWork(): Result {
//            return Result.success()
//        }
//    }
//
//
//    private fun messageTest() {
//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                return@addOnCompleteListener
//            }
//
//            val token = task.result
//
//            val msg = "FCM Registration token: $token"
//            Log.d(TAG, msg)
//        }
//    }
//
//
//    fun sendNotifications(
//        accessToken: String,
//        token: String,
//        title: String,
//        content: String
//    ) {
//        val api =
//            "https://fcm.googleapis.com/v1/projects/project-sns-58aea/messages:send"
//
//        val url = api.toHttpUrlOrNull()!!.newBuilder().build()
//        val client = OkHttpClient()
//
//        val json = JSONObject().apply {
//            put("message", JSONObject().apply {
//                put("token", token)
//                put("notification", JSONObject().apply {
//                    put("title", title)
//                    put("body", content)
//                })
//            })
//        }
//
//        val body =
//            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
//
//
//        val request = Request.Builder()
//            .url(url)
//            .post(body)
//            .addHeader("Authorization", "Bearer $accessToken")
//            .addHeader("Content-Type", "application/json")
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                // Handle success
//                val responseBody = response.body.string()
//                println("FCM Response: $responseBody")
//            }
//        })
//
//    }
//}

class FirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMessagingServ"
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification!!.title.toString()
        val body = message.notification!!.body.toString()
        val data = message.data
        Log.d(TAG, "onMessageReceived: title = $title, body = $body, data = $data")

        FcmUtil.showNotification(
            context = applicationContext,
            title = title,
            body = body
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken() called with: token = $token")
    }

    fun sendNotifications(
        accessToken: String,
        token: String,
        title: String,
        content: String
    ) {
        val api =
            "https://fcm.googleapis.com/v1/projects/project-sns-58aea/messages:send"

        val url = api.toHttpUrlOrNull()!!.newBuilder().build()
        val client = OkHttpClient()

        val json = JSONObject().apply {
            put("message", JSONObject().apply {
                put("token", token)
                put("notification", JSONObject().apply {
                    put("title", title)
                    put("body", content)
                })
            })
        }

        val body =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())


        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle success
                val responseBody = response.body.string()
                println("FCM Response: $responseBody")
            }
        })
    }

    fun getAccessToken(): String {
        val asset = resources.assets.open("admin.json")
        val googleCredential = GoogleCredentials.fromStream(asset)
            .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
        googleCredential.refresh()
        val accessToken = googleCredential.accessToken.tokenValue
        Log.d("accessToken", accessToken)
        return accessToken
    }
}

object FcmUtil {

    private const val TAG = "FcmUtil"

    fun initFcm(context: Context) {
        FirebaseApp.initializeApp(context)
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            Log.d(TAG, "initFcm: token : ${it.result}")
        }
    }

    fun showNotification(context: Context, title: String, body: String) {
        val pendingIntent = PendingIntent.getActivity(
            context,
            100,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification: Notification?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification =
                NotificationCompat.Builder(context, "mwc")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .build()
        } else {
            notification =
                NotificationCompat.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .build()
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
}

