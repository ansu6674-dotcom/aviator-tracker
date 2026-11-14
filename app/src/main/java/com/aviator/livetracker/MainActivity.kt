package com.aviator.livetracker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.view.Gravity
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aviator.livetracker.capture.ScreenCaptureService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private val projectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val svcIntent = Intent(this, ScreenCaptureService::class.java).apply {
                putExtra("RESULT_CODE", result.resultCode)
                putExtra("DATA", result.data)
            }
            startForegroundService(svcIntent)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = FrameLayout(this)
        val webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = WebViewClient()
            loadUrl(BuildConfig.TRACKER_BASE_URL)
        }
        root.addView(webView)

        val ocrBtn = Button(this).apply {
            text = "Start Screen OCR"
            setOnClickListener { promptScreenCaptureConsent() }
        }
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.END
            setMargins(32, 32, 32, 48)
        }
        root.addView(ocrBtn, params)
        setContentView(root)
    }

    private fun promptScreenCaptureConsent() {
        AlertDialog.Builder(this)
            .setTitle("Screen Reading Permission / स्क्रीन पढ़ने की अनुमति")
            .setMessage(
                "Aviator Live Tracker only reads finished round multipliers from your screen to save historical records. " +
                "It never predicts future rounds or guarantees wins. Allow screen OCR?"
            )
            .setPositiveButton("Allow / अनुमति दें") { _, _ ->
                val mgr = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                projectionLauncher.launch(mgr.createScreenCaptureIntent())
            }
            .setNegativeButton("Cancel / रद्द करें", null)
            .show()
    }
}

object ApiSyncClient {
    private val client = OkHttpClient()

    fun pushRound(
        multiplier: Double?,
        status: String,
        confidence: Int,
        rawOcrText: String?,
        timestampIso: String
    ): Boolean {
        return try {
            val bodyJson = JSONObject().apply {
                if (multiplier != null) put("multiplier", multiplier) else put("multiplier", JSONObject.NULL)
                put("status", status)
                put("source", "ANDROID_SYNC")
                put("confidence", confidence)
                put("rawOcrText", rawOcrText ?: "")
                put("timestamp", timestampIso)
            }
            val req = Request.Builder()
                .url("${BuildConfig.TRACKER_BASE_URL}/api/rounds")
                .post(bodyJson.toString().toRequestBody("application/json".toMediaType()))
                .build()
            client.newCall(req).execute().use { it.isSuccessful }
        } catch (_: Exception) {
            false
        }
    }
}
