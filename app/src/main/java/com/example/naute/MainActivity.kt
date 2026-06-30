package com.example.naute

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.naute.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val permissionsArray = arrayOf(Manifest.permission.RECORD_AUDIO)
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent
    private lateinit var binding: ActivityMainBinding
    private var dialogView: View? = null
    private var alertDialog: AlertDialog? = null

    private fun showTranscriptionDialog() {
        dialogView = layoutInflater.inflate(R.layout.live_transcription_dialog, null)
        val builder = AlertDialog.Builder(this)

        val addBtn = dialogView?.findViewById<ImageButton>(R.id.addBtn)
        val discardBtn = dialogView?.findViewById<ImageButton>(R.id.discardBtn)

        discardBtn?.setOnClickListener {
            alertDialog?.dismiss()
        }

        builder.setView(dialogView)
        alertDialog = builder.create()
        alertDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog?.show()
    }

    private fun updateDialogText(text: String) {
        runOnUiThread {
            dialogView?.findViewById<TextView>(R.id.transcriptionText)?.text = text
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.foldersBtn.setOnClickListener {
            val intent = Intent(this, folders::class.java)
            startActivity(intent)
        }

        binding.preferencesBtn.setOnClickListener {
            val intent = Intent(this, preferences::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (ActivityCompat.checkSelfPermission(this, permissionsArray[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissionsArray, 200)
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {
                Log.d("NAUTE", "Ready!")
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(p0: Float) {}
            override fun onBufferReceived(p0: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {}
            override fun onResults(results: Bundle?) {}
            override fun onPartialResults(partialResults: Bundle?) {
                val data = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!data.isNullOrEmpty()) {
                    updateDialogText(data[0])
                }
            }
            override fun onEvent(p0: Int, p1: Bundle?) {}
        })

        binding.createNauteBtn.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    showTranscriptionDialog()
                    speechRecognizer.startListening(speechRecognizerIntent)
                    binding.createNauteBtn.setImageResource(R.drawable.create_naute_icon_pressed)
                    binding.introText.text = ""

                    true
                }
                MotionEvent.ACTION_UP -> {
                    speechRecognizer.stopListening()
                    binding.createNauteBtn.setImageResource(R.drawable.create_naute_icon)

                    val addBtn = dialogView?.findViewById<ImageButton>(R.id.addBtn)
                    val discardBtn = dialogView?.findViewById<ImageButton>(R.id.discardBtn)

                    addBtn?.visibility = View.VISIBLE
                    discardBtn?.visibility = View.VISIBLE

                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }

}