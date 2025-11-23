package com.example.solat.util

import java.time.Instant

object SolatDateTime {
    fun nowEpochMilli(): Long {
        return Instant.now().toEpochMilli()
    }
}
