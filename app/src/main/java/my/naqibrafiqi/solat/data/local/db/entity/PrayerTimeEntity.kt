package my.naqibrafiqi.solat.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prayer_times",
    indices = [
        Index(value = ["prayer_details_id"]),
        Index(value = ["prayer_details_id", "day"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = PrayerDetailsEntity::class,
            parentColumns = ["prayer_details_id"],
            childColumns = ["prayer_details_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.RESTRICT
        )
    ]
)
data class PrayerTimeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "prayer_time_id")
    val prayerTimeId: Int = 0,
    @ColumnInfo(name = "prayer_details_id")
    val prayerDetailsId: Int,
    val day: Int,
    val hijri: String,
    val fajr: Long,
    val syuruk: Long,
    val dhuhr: Long,
    val asr: Long,
    val maghrib: Long,
    val isha: Long,
    @ColumnInfo(name = "created_date")
    val createdDate: Long,
    @ColumnInfo(name = "updated_date")
    val updatedDate: Long? = null,
    @ColumnInfo(name = "deleted_date")
    val deletedDate: Long? = null
)