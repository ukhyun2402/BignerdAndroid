package com.wookhyun.geoquiz

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wookhyun.geoquiz.databinding.ActivityCheatBinding

private const val EXTRA_ANSWER_IS_TRUE = "com.wookhyun.android.geoquiz.answer_is_true"
const val EXTRA_ANSWER_SHOWN = "com.wookhyun.android.geoquiz.answer_is_true"

class CheatActivity : AppCompatActivity() {
    lateinit var binding: ActivityCheatBinding
    private var answerIsTrue = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityCheatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        binding.apiLevelText.setText("API Level ${Build.VERSION.SDK_INT}")
        binding.showAnswerButtons.setOnClickListener {
            val answerText = when {
                answerIsTrue -> R.string.true_btn_string
                else -> R.string.false_btn_string
            }
            binding.answerTextView.setText(answerText)
            setAnswerShownResult(true)
        }

    }
    fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(RESULT_OK, data)
    }

    companion object{
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                this.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}