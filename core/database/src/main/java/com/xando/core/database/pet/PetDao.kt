package com.xando.core.database.pet

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update
import androidx.room3.Upsert
import com.xando.core.database.pet.PetViewerRoles.CO_OWNER
import com.xando.core.database.pet.PetViewerRoles.OWNER
import com.xando.core.database.user.UserEntity
import com.xando.core.database.user.UserSnippetEntity
import kotlinx.coroutines.flow.Flow

/**
 * Доступ к питомцам и связанным с ними данным: интересам, владельцам и отметкам пользователей.
 */
@Dao
abstract class PetDao {

    /**
     * Питомцы текущего пользователя — те, которыми он владеет или совладеет, в порядке создания.
     */
    @Query("SELECT * FROM pets WHERE viewer_role IN ('$OWNER', '$CO_OWNER') ORDER BY created_at")
    abstract fun observeOwnPets(): Flow<List<PetEntity>>

    /**
     * Питомец со всеми связанными данными; `null`, если питомца нет в базе.
     */
    @Transaction
    @Query("SELECT * FROM pets WHERE id = :petId")
    abstract fun observePet(petId: String): Flow<PetWithDetails?>

    /**
     * Приводит питомцев текущего пользователя к [pets]: сохраняет пришедших вместе с интересами и
     * удаляет своих питомцев, которых среди них нет. Чужих питомцев не трогает, отметки пользователей
     * сохраняет.
     *
     * @param pets Профили питомцев текущего пользователя.
     * @param interests Интересы всех [pets].
     */
    @Transaction
    open suspend fun replaceOwnPets(pets: List<PetProfileEntity>, interests: List<PetInterestEntity>) {
        val petIds = pets.map { it.id }
        deleteOwnPetsExcept(petIds)
        upsertProfiles(pets)
        deleteInterests(petIds)
        insertInterests(interests)
    }

    /**
     * Сохраняет питомца со всеми связанными данными, заменяя прежние интересы, владельцев и превью
     * отметок.
     *
     * @param pet Профиль питомца.
     * @param relations Отметки пользователей о питомце.
     * @param interests Интересы питомца.
     * @param users Пользователи из [owners] и [relationPreviews].
     * @param owners Владельцы питомца.
     * @param relationPreviews Превью отметок.
     */
    @Transaction
    open suspend fun savePet(
        pet: PetProfileEntity,
        relations: PetRelationsEntity,
        interests: List<PetInterestEntity>,
        users: List<UserSnippetEntity>,
        owners: List<PetOwnerEntity>,
        relationPreviews: List<PetRelationPreviewEntity>,
    ) {
        upsertProfiles(listOf(pet))
        updateRelations(relations)
        deleteInterests(listOf(pet.id))
        insertInterests(interests)
        upsertUsers(users)
        deleteOwners(pet.id)
        insertOwners(owners)
        deleteRelationPreviews(pet.id)
        insertRelationPreviews(relationPreviews)
    }

    /**
     * Меняет отметку текущего пользователя о питомце.
     *
     * @param petId Идентификатор питомца.
     * @param relation Код отметки; `null` — отметки нет.
     */
    @Query("UPDATE pets SET my_relation = :relation WHERE id = :petId")
    abstract suspend fun updateMyRelation(petId: String, relation: String?)

    /**
     * Удаляет питомца вместе с его интересами, владельцами и превью отметок.
     */
    @Query("DELETE FROM pets WHERE id = :petId")
    abstract suspend fun deletePet(petId: String)

    /**
     * Удаляет питомцев текущего пользователя, кроме [keepIds].
     */
    @Query("DELETE FROM pets WHERE viewer_role IN ('$OWNER', '$CO_OWNER') AND id NOT IN (:keepIds)")
    protected abstract suspend fun deleteOwnPetsExcept(keepIds: List<String>)

    /**
     * Сохраняет профили питомцев, обновляя у существующих только поля [PetProfileEntity].
     */
    @Upsert(entity = PetEntity::class)
    protected abstract suspend fun upsertProfiles(pets: List<PetProfileEntity>)

    /**
     * Обновляет у сохранённого питомца только поля [PetRelationsEntity].
     */
    @Update(entity = PetEntity::class)
    protected abstract suspend fun updateRelations(relations: PetRelationsEntity)

    /**
     * Удаляет все интересы питомцев [petIds].
     */
    @Query("DELETE FROM pet_interests WHERE pet_id IN (:petIds)")
    protected abstract suspend fun deleteInterests(petIds: List<String>)

    /** @SelfDocumented */
    @Insert
    protected abstract suspend fun insertInterests(interests: List<PetInterestEntity>)

    /**
     * Сохраняет пользователей, обновляя у существующих только поля [UserSnippetEntity].
     */
    @Upsert(entity = UserEntity::class)
    protected abstract suspend fun upsertUsers(users: List<UserSnippetEntity>)

    /**
     * Удаляет всех владельцев питомца [petId].
     */
    @Query("DELETE FROM pet_owners WHERE pet_id = :petId")
    protected abstract suspend fun deleteOwners(petId: String)

    /** @SelfDocumented */
    @Insert
    protected abstract suspend fun insertOwners(owners: List<PetOwnerEntity>)

    /**
     * Удаляет всё превью отметок питомца [petId].
     */
    @Query("DELETE FROM pet_relation_previews WHERE pet_id = :petId")
    protected abstract suspend fun deleteRelationPreviews(petId: String)

    /** @SelfDocumented */
    @Insert
    protected abstract suspend fun insertRelationPreviews(previews: List<PetRelationPreviewEntity>)
}
