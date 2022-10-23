package com.wookhyun.geoquiz

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val TAG = "QuizVIewModel"
const val CURRENT_INDEX_KEY: String = "CURRENT_INDEX_KEY"
const val IS_CHEATER_KEY: String = "IS_CHEATER_KEY"

class QuizVIewModel(val savedStateHandle: SavedStateHandle): ViewModel() {

    private val questionBank = listOf(
        Question(R.string.question_australia, true, false),
        Question(R.string.question_oceans, true, false),
        Question(R.string.question_mideast, false, false),
        Question(R.string.question_africa, false, false),
        Question(R.string.question_americas, true, false),
        Question(R.string.question_asia, true, false)
    )

    private var currentIndex: Int
        get() = savedStateHandle[CURRENT_INDEX_KEY] ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    val currentQuestionAnswer : Boolean
        get() = questionBank[currentIndex].answerTrue

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    var isCheater:Boolean
        get() = questionBank[currentIndex].usedCheat
        set(value) {
            if(value){
                questionBank[currentIndex].usedCheat = true
            }
            return savedStateHandle.set(IS_CHEATER_KEY, value)
        }

    val countUsingCheat: Int
        get() = questionBank.count { it.usedCheat }

    fun moveToNext(){
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev(){
        currentIndex = if(currentIndex == 0) questionBank.lastIndex else currentIndex - 1
    }
}