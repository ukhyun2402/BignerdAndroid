package com.wookhyun.criminalintent

class Solution {
    fun solution(lottos: IntArray, win_nums: IntArray): IntArray {
        return intArrayOf(
            lottos.filter{win_nums.contains(it) || it == 0}.size,
            lottos.filter(win_nums::contains).size).map { if (it >= 2) 7 - it else 6 }.toIntArray()
    }
}