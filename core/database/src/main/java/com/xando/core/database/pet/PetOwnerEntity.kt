package com.xando.core.database.pet

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import com.xando.core.database.user.UserEntity

/**
 * Место пользователя в списке владельцев питомца.
 *
 * @property petId Идентификатор питомца.
 * @property userId Идентификатор пользователя.
 * @property role Код роли: основной владелец, совладелец или приглашённый. Приглашённых сервер
 * отдаёт только основному владельцу.
 * @property position Позиция в списке владельцев в том порядке, в котором его отдал сервер.
 */
@Entity(
    tableName = "pet_owners",
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
data class PetOwnerEntity(
    @ColumnInfo(name = "pet_id") val petId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    val role: String,
    val position: Int,
)
