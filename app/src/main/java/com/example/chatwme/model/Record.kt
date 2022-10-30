package com.example.chatwme.model

import com.google.firebase.Timestamp

data class Record(
    val text: String? = null,
    val name: String? = null,
    val photoUrl: String? = null,
    val imageUrl: String? = null,
    val time: Long?=0L
)