package com.xando.core.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.xando.core.database.pet.PetDao
import com.xando.core.database.pet.PetEntity
import com.xando.core.database.pet.PetInterestEntity
import com.xando.core.database.pet.PetOwnerEntity
import com.xando.core.database.pet.PetRelationPreviewEntity
import com.xando.core.database.user.UserEntity

/**
 * Локальная база данных приложения.
 *
 * Не видна за пределами модуля: снаружи доступны только DAO.
 */
@Database(
    entities = [
        PetEntity::class,
        PetInterestEntity::class,
        PetOwnerEntity::class,
        PetRelationPreviewEntity::class,
        UserEntity::class,
    ],
    version = 1,
)
internal abstract class StayaDatabase : RoomDatabase() {

    /** @SelfDocumented */
    abstract fun petDao(): PetDao
}
