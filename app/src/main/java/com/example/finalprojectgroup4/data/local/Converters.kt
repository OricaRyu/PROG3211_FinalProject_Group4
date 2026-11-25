package com.example.finalprojectgroup4.data.local

import androidx.room.TypeConverter
import com.example.finalprojectgroup4.data.model.Category

/**
 * Tells Room how to store a Category in the table and read it back.
 * Without this, Room's built-in enum handling crashes on an unknown value;
 * this version falls back to OTHER instead (Category.fromStored).
 *
 * Kind: class. Owner: Track B (Ana).
 */
class Converters {

    /** Category -> text saved in the table ("RESTAURANT"). */
    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    /** Text from the table -> Category. Unknown or missing values become OTHER. */
    @TypeConverter
    fun toCategory(value: String?): Category = Category.fromStored(value)
}