package com.xando.core.database.pet

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import com.xando.core.database.user.UserEntity

/**
 * Пользователь из превью отметок питомца: одного из тех, кто дружит или не дружит с ним.
 *
 * @property petId Идентификатор питомца.
 * @property userId Идентификатор пользователя.
 * @property relation Код отметки пользователя.
 * @property position Позиция в превью своей отметки в том порядке, в котором его отдал сервер.
 */
@Entity(
    tableName = "pet_relation_previews",
    primaryKeys = ["pet_id", "user_id"],
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["pet_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("user_id")],
)
data class PetRelationPreviewEntity(
    @ColumnInfo(name = "pet_id") val petId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    val relation: String,
    val position: Int,
)
