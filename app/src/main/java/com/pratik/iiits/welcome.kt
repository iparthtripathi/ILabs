package com.pratik.iiits

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.google.android.exoplayer2.ui.PlayerView
import com.pratik.iiits.databinding.ActivityWelcomeBinding

class welcome : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerView = binding.playerView

        // Initialize ExoPlayer
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        // Set the media item to be played from raw resource
        val rawVideoUri = RawResourceDataSource.buildRawResourceUri(R.raw.background_video)
        val mediaItem = MediaItem.fromUri(rawVideoUri)
        player.setMediaItem(mediaItem)

        // Set repeat mode to loop the video
        player.repeatMode = ExoPlayer.REPEAT_MODE_ALL

        // Prepare the player and start the playback automatically
        player.prepare()
        player.playWhenReady = true
        player.setVolume(0f)
        binding.getStartedButton.setOnClickListener {
            startActivity(Intent(this, login_user::class.java))
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
    fun start(view:View) {
        startActivity(Intent(this, login_user::class.java))
    }
}
