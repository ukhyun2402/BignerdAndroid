package com.wookhyun.geoquiz

import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.wookhyun.geoquiz.databinding.ActivityMainBinding
import kotlin.math.abs

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val quizVIewModel: QuizVIewModel by viewModels()

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            quizVIewModel.isCheater =
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            updateCheatCount()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateQuestion()

        binding.btnTrue.setOnClickListener {
            checkAnswer(true)
            disableAnswerButtons()
        }
        binding.btnFalse.setOnClickListener {
            checkAnswer(false)
            disableAnswerButtons()
        }

        binding.btnNext.setOnClickListener {
//            currentIndex = (currentIndex + 1) % questionBank.size
            quizVIewModel.moveToNext()
            updateQuestion()
        }
        binding.btnPrev.setOnClickListener {
//            currentIndex = if(currentIndex == 0) questionBank.size - 1 else currentIndex - 1
            quizVIewModel.moveToPrev()
            updateQuestion()
        }

        binding.cheatButton.setOnClickListener {
            cheatLauncher.launch(CheatActivity.newIntent(this, quizVIewModel.currentQuestionAnswer))
        }
        updateCheatCount()
    }

    private fun disableAnswerButtons() {
        binding.btnTrue.isEnabled = false
        binding.btnFalse.isEnabled = false
    }

    private fun updateQuestion() {
        // clear cheat history
        binding.quizText.setText(quizVIewModel.currentQuestionText)
        binding.btnTrue.isEnabled = true
        binding.btnFalse.isEnabled = true
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizVIewModel.currentQuestionAnswer
        val answerMessage = when {
            quizVIewModel.isCheater -> R.string.judgement_toast
            userAnswer == correctAnswer -> R.string.correct_string
            else -> R.string.incorrect_string
        }
        Snackbar.make(binding.root, answerMessage, Snackbar.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun blurCheatButton() {
        val effect = RenderEffect.createBlurEffect(
            10.0f,
            10.0f,
            Shader.TileMode.CLAMP
        )
        binding.cheatButton.setRenderEffect(effect)
    }

    private fun updateCheatCount(){
        binding.countCheatText.setText("${quizVIewModel.countUsingCheat} / 3")
        if(quizVIewModel.countUsingCheat > 2) {
            if(Build.VERSION_CODES.S <= Build.VERSION.SDK_INT){
                blurCheatButton()
            }
            binding.cheatButton.isEnabled = false
        }
    }
}