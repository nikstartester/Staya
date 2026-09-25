package com.xando.core.database.pet

import androidx.room3.Embedded
import androidx.room3.Relation
import com.xando.core.database.user.UserEntity

/**
 * Питомец со всеми связанными данными: для карточки и формы редактирования.
 *
 * @property pet Питомец.
 * @property interestCodes Коды интересов.
 * @property owners Владельцы в произвольном порядке; порядок задаёт [PetOwnerEntity.position].
 * @property relationPreviews Превью отметок в произвольном порядке; порядок внутри отметки задаёт
 * [PetRelationPreviewEntity.position].
 */
data class PetWithDetails(
    @Embedded val pet: PetEntity,
    @Relation(
        entity = PetInterestEntity::class,
        parentColumns = ["id"],
        entityColumns = ["pet_id"],
        projection = ["code"],
    )
    val interestCodes: List<String>,
    @Relation(
        entity = PetOwnerEntity::class,
        parentColumns = ["id"],
        entityColumns = ["pet_id"],
    )
    val owners: List<PetOwnerWithUser>,
    @Relation(
        entity = PetRelationPreviewEntity::class,
        parentColumns = ["id"],
        entityColumns = ["pet_id"],
    )
    val relationPreviews: List<PetRelationPreviewWithUser>,
)

/**
 * Владелец питомца вместе с данными пользователя.
 *
 * @property owner Место пользователя в списке владельцев.
 * @property user Пользователь.
 */
data class PetOwnerWithUser(
    @Embedded val owner: PetOwnerEntity,
    @Relation(
        parentColumns = ["user_id"],
        entityColumns = ["id"],
    )
    val user: UserEntity,
)

/**
 * Пользователь из превью отметок вместе с его данными.
 *
 * @property preview Место пользователя в превью.
 * @property user Пользователь.
 */
data class PetRelationPreviewWithUser(
    @Embedded val preview: PetRelationPreviewEntity,
    @Relation(
        parentColumns = ["user_id"],
        entityColumns = ["id"],
    )
    val user: UserEntity,
)
