package com.example.naute

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.ImageButton
import android.widget.Chronometer
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.naute.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date


class MainActivity : AppCompatActivity() {
    private val permissionsArray = arrayOf(Manifest.permission.RECORD_AUDIO)
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent
    private lateinit var binding: ActivityMainBinding
    private var foldersView: View? = null
    private var dialogView: View? = null
    private var alertDialog: AlertDialog? = null
    private val chronometer: Chronometer? = null
    private val nautes = ArrayList<Naute>()
    val nAdapter = NauteAdapter(this, nautes)

    private fun showTranscriptionDialog() {
        dialogView = layoutInflater.inflate(R.layout.live_transcription_dialog, null)
        foldersView = layoutInflater.inflate(R.layout.activity_folders, null)

        val builder = AlertDialog.Builder(this)

        val addBtn = dialogView?.findViewById<ImageButton>(R.id.addBtn)
        val discardBtn = dialogView?.findViewById<ImageButton>(R.id.discardBtn)
        val folderSize = foldersView?.findViewById<TextView>(R.id.folderSize)
        val folderDate = foldersView?.findViewById<TextView>(R.id.folderDate)
        var n = 1

        addBtn?.setOnClickListener {
            val transcriptionText = dialogView?.findViewById<TextView>(R.id.transcriptionText)
            var text = transcriptionText?.text.toString()

            chronometer?.stop()
            var duration = chronometer?.text.toString()
            n++
            n.toString()

            val d = Date()
            val s = SimpleDateFormat("dd MMMM yyyy")
            val date = s.format(d)

            if (text == "Listening..") {
                text = "Unrecorded Audio"
                duration = "00:00"
            }
            text = "Untitled"

            nautes.add(0, Naute(text, date, duration))
            nAdapter.notifyItemInserted(0)
            binding.recyclerView.scrollToPosition(0)

            if (n > 1) {
                folderSize?.text = "$n naute"
            } else {
                folderSize?.text = "$n nautes"
            }
            folderDate?.text = date

            alertDialog?.dismiss()
        }

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

        binding.recyclerView.adapter = nAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

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
            override fun onReadyForSpeech(p0: Bundle?) {}
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
                    val chronometer = dialogView?.findViewById<Chronometer>(R.id.nauteLength)
                    chronometer?.base = SystemClock.elapsedRealtime()
                    chronometer?.start()

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