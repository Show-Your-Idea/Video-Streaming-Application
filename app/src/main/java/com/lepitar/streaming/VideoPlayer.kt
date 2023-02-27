package com.lepitar.streaming

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.util.MimeTypes
import com.lepitar.streaming.databinding.ActivityVideoPlayerBinding
import java.util.concurrent.TimeUnit


class VideoPlayer : AppCompatActivity() {

    lateinit var binding: ActivityVideoPlayerBinding
    lateinit var videoPlayer: ExoPlayer
    lateinit var trackSelector: DefaultTrackSelector
    lateinit var loadControl: DefaultLoadControl
    lateinit var exoPlayerCache: Cache
    private var url = ""
    private var episodeTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideNavigationBar()
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        url = intent.getStringExtra("url") ?: ""
        episodeTitle = intent.getStringExtra("title") ?: "나의 히어로 아카데미아 1기 1화"

        findViewById<TextView>(R.id.title).text = episodeTitle
        if (url.isEmpty()) {
            Toast.makeText(this, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            finish()
        }

//        findViewById<ProgressBar>(R.id.exo_buffering).visibility = View.VISIBLE
//        Log.d("VISIBILE", binding.exoBuffering.visibility.toString())
        initVideoPlayer()
        buildMediaSource().let {
            videoPlayer.playWhenReady = true
            videoPlayer.setMediaSource(it)
            videoPlayer.prepare()
        }
    }

    private fun hideNavigationBar() {
        val uiOptions = window.decorView.systemUiVisibility
        var newUiOptions = uiOptions
        val isImmersiveModeEnabled = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == uiOptions
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = newUiOptions
    }

    private fun initVideoPlayer() {
        trackSelector = DefaultTrackSelector(this)

        loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(/* minBufferTime */ TimeUnit.SECONDS.toMillis(10).toInt(), /* maxBufferTime */ TimeUnit.SECONDS.toMillis(180).toInt(),
                /* bufferForPlaybackMs */ DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS, /* bufferForPlaybackAfterRebufferMs */ DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS)
            .setBackBuffer(TimeUnit.MINUTES.toMillis(3).toInt(), false)
            .build()

        videoPlayer = ExoPlayer.Builder(this)
            .setSeekBackIncrementMs(10000)
            .setSeekForwardIncrementMs(10000)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .build()

        findViewById<ImageView>(R.id.closeButton).setOnClickListener {
            finish()
        }

//        videoPlayer.addListener(object: Player.Listener {
//            override fun onPlaybackStateChanged(playbackState: Int) {
//                super.onPlaybackStateChanged(playbackState)
//                Log.d("STATE", playbackState.toString())
//                if (playbackState == Player.STATE_BUFFERING) {
//                    binding.exoBuffering.visibility = View.VISIBLE
//                } else if (playbackState == Player.STATE_READY) {
//                    binding.exoBuffering.visibility = View.INVISIBLE
//                }
//            }
//        })

        binding.exoPlayer.player = videoPlayer
    }

    private fun buildMediaSource(): MediaSource {
        //Range bytes=0-
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(hashMapOf("Range" to "bytes=0-"))

        //turn off subtitles
        trackSelector.parameters = DefaultTrackSelector.Parameters.Builder(this).setRendererDisabled(C.TRACK_TYPE_VIDEO, true).build()
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.Builder()
                .setCustomCacheKey(url)
                .setUri(url)
                .setMimeType(MimeTypes.APPLICATION_MPD)
                .build())
    }

    // 일시중지
    override fun onResume() {
        super.onResume()
        videoPlayer.playWhenReady = true
    }

    // 정지
    override fun onStop() {
        super.onStop()
        videoPlayer.playWhenReady = false
        if (isFinishing) {
            releasePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        videoPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        videoPlayer.pause()
    }

    // 종료
    private fun releasePlayer() {
        videoPlayer.release()
    }
}