package com.xando.core.database.pet

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey

/**
 * Интерес питомца.
 *
 * @property petId Идентификатор питомца.
 * @property code Код интереса.
 */
@Entity(
    tableName = "pet_interests",
    primaryKeys = ["pet_id", "code"],
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["pet_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class PetInterestEntity(
    @ColumnInfo(name = "pet_id") val petId: String,
    val code: String,
)
