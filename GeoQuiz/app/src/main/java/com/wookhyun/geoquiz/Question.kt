package com.wookhyun.geoquiz

import androidx.annotation.StringRes

data class Question(@StringRes val textResId: Int, val answerTrue : Boolean, var usedCheat: Boolean)
