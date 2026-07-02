package my.naqibrafiqi.solat.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "zones",
    indices = [Index(value = ["code"], unique = true)]
)
data class ZoneEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "zone_id")
    val zoneId: Int = 0,
    val code: String,
    val state: String,
    val district: String,
    @ColumnInfo(name = "created_date")
    val createdDate: Long,
    @ColumnInfo(name = "updated_date")
    val updatedDate: Long? = null,
    @ColumnInfo(name = "deleted_date")
    val deletedDate: Long? = null
)