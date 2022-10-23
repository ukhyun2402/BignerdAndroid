package com.wookhyun.geoquiz

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.*
import org.junit.Test

class QuizVIewModelTest{
    @Test
    fun provideExpectedQuestionText(){
        val savedStateHandle = SavedStateHandle(mapOf(CURRENT_INDEX_KEY to 5))
        val quizVIewModel = QuizVIewModel(savedStateHandle)
        assertEquals(R.string.question_asia, quizVIewModel.currentQuestionText)
        quizVIewModel.moveToNext()
        assertEquals(R.string.question_australia, quizVIewModel.currentQuestionText)
    }
}