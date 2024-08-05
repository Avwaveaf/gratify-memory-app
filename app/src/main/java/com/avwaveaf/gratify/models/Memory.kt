package com.avwaveaf.gratify.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Entity("memory_data")
@Parcelize
data class Memory(
    @PrimaryKey
    @ColumnInfo("memory_id")
    val memoryId: UUID = UUID.randomUUID(),
    @ColumnInfo("memory_title")
    val memoryTitle: String,
    @ColumnInfo("memory_detail")
    val memoryDetail: String,
    @ColumnInfo("date")
    val date: Date,
    @ColumnInfo("is_favorite")
    val isFavorite:Boolean
):Parcelable
