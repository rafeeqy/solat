package com.example.solat.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prayer_details",
    indices = [
        Index(value = ["zone_id"]),
        Index(value = ["zone_id", "year", "month"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = Zone::class,
            parentColumns = ["zone_id"],
            childColumns = ["zone_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.RESTRICT
        )
    ]
)
data class PrayerDetails(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "prayer_details_id")
    val prayerDetailsId: Int = 0,
    @ColumnInfo(name = "zone_id")
    val zoneId: Int,
    val year: Int,
    val month: String,
    @ColumnInfo(name = "month_number")
    val monthNumber: Int,
    @ColumnInfo(name = "api_last_updated")
    val apiLastUpdated: String? = null,
    @ColumnInfo(name = "created_date")
    val createdDate: Long,
    @ColumnInfo(name = "updated_date")
    val updatedDate: Long? = null,
    @ColumnInfo(name = "deleted_date")
    val deletedDate: Long? = null
)