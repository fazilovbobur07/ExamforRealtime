package com.example.examforrealtime

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.examforrealtime.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var Player: MediaPlayer
    private lateinit var seekBar: SeekBar
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var database: DatabaseReference
    private lateinit var ringtoneStateListener: ValueEventListener

    private val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        }

        seekBar = findViewById(R.id.my_sikbar)
        playButton = findViewById(R.id.my_play)
        pauseButton = findViewById(R.id.my_pause)

        database = FirebaseDatabase.getInstance().reference.child("ringtone_state")

        playButton.setOnClickListener {
            if (!Player.isPlaying) {
                val uri = Uri.parse("content://settings/system/ringtone")
                Player.setDataSource(applicationContext, uri)
                Player.prepare()
                Player.start()
                playButton.isEnabled = false
                pauseButton.isEnabled = true
                database.setValue(true)
            }
        }

        pauseButton.setOnClickListener {
            if (Player.isPlaying) {
                Player.pause()
                playButton.isEnabled = true
                pauseButton.isEnabled = false
                database.setValue(false)
            }
        }

        ringtoneStateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val Playing = dataSnapshot.getValue(Boolean::class.java) ?: false
                if (Playing) {
                    Player.start()
                    playButton.isEnabled = false
                    pauseButton.isEnabled = true
                } else {
                    Player.pause()
                    playButton.isEnabled = true
                    pauseButton.isEnabled = false
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        database.addValueEventListener(ringtoneStateListener)
    }
}



