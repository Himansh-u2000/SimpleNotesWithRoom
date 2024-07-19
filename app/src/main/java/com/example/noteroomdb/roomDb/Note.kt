package com.example.noteroomdb.roomDb

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val noteName: String,
    val noteBody: String
)
