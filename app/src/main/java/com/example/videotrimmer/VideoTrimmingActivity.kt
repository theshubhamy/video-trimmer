package com.example.videotrimmer

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.videotrimmer.databinding.ActivityVideoTrimmingBinding
import com.redevrx.video_trimmer.event.OnVideoEditedEvent
import java.io.File


class VideoTrimmingActivity : AppCompatActivity(), OnVideoEditedEvent {
    private lateinit var FOLDER_PATH_TRIM_VIDEO_SAVER: File
    private var _binding: ActivityVideoTrimmingBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressDialog: ProgressDialog

    companion object {
        @JvmStatic
        fun startActivityForResult(context: Activity, path: String, resultCode: Int) {
            val starter = Intent(context, VideoTrimmingActivity::class.java)
                .putExtra("videoPath", path)
            context.startActivityForResult(starter, resultCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoTrimmingBinding.inflate(LayoutInflater.from(this))
        setContentView(_binding?.root)

        val path = intent.getStringExtra("videoPath") ?: ""
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Crop")
        progressDialog.setCancelable(false)

        FOLDER_PATH_TRIM_VIDEO_SAVER = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "TRIM VIDEOS"
        )

        if (!FOLDER_PATH_TRIM_VIDEO_SAVER.exists())
            FOLDER_PATH_TRIM_VIDEO_SAVER.mkdir()

        try {
            setupVideoTrimmer(path)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.btnSaveVideo.setOnClickListener {
            binding.videoTrimmer.saveVideo()
            progressDialog.show()
        }

    }

    private fun setupVideoTrimmer(path: String) {
        val selectedUri = Uri.parse(path)
        binding.videoTrimmer.apply {
            setVideoBackgroundColor(resources.getColor(R.color.white))
            setOnTrimVideoListener(this@VideoTrimmingActivity)
            setVideoURI(selectedUri)
            setDestinationPath(FOLDER_PATH_TRIM_VIDEO_SAVER.absolutePath)
            setVideoInformationVisibility(true)
            setMaxDuration(30)
            setMinDuration(0)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun getResult(uri: Uri) {
        progressDialog.dismiss()
        val intent = Intent()
        intent.putExtra("TRIMMED_VIDEO_URI", uri.toString())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onError(message: String) {
        progressDialog.dismiss()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}