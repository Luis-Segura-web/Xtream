package com.kybers.xtream.data.database

import androidx.room.TypeConverter
import com.kybers.xtream.data.entities.VodType

class Converters {
    
    @TypeConverter
    fun fromVodType(vodType: VodType): String {
        return vodType.name
    }
    
    @TypeConverter
    fun toVodType(vodType: String): VodType {
        return VodType.valueOf(vodType)
    }
}