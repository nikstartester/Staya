package com.xando.data.pet

import com.xando.core.database.pet.PetEntity
import com.xando.core.database.pet.PetInterestEntity
import com.xando.core.database.pet.PetOwnerEntity
import com.xando.core.database.pet.PetOwnerWithUser
import com.xando.core.database.pet.PetViewerRoles
import com.xando.core.database.pet.PetWithDetails
import com.xando.core.database.user.UserEntity
import com.xando.core.database.user.UserSnippetEntity
import com.xando.core.models.pet.PetBreed
import com.xando.core.models.pet.PetDetail
import com.xando.core.models.pet.PetInterest
import com.xando.core.models.pet.PetOwner
import com.xando.core.models.pet.PetOwnerRole
import com.xando.core.models.pet.PetSex
import com.xando.core.models.pet.PetStatus
import com.xando.core.models.pet.PetSummary
import com.xando.core.models.pet.PetViewerRole
import com.xando.core.models.user.UserSummary
import com.xando.data.pet.model.PetCardResponse
import com.xando.data.pet.model.PetProfileResponse
import com.xando.data.pet.model.UserSnippetResponse
import java.time.OffsetDateTime

// DTO → Entity. Коды пишутся в базу как есть, в том числе неизвестные этой версии приложения: их
// судьбу решает маппинг в домен.

/** Маппинг [PetProfileResponse] в [PetEntity]. */
internal fun PetProfileResponse.toEntity() = PetEntity(
    id = id,
    name = name,
    birthDate = birthDate,
    breed = breed,
    sex = sex.name,
    weightGrams = weightGrams,
    description = description,
    status = status,
    photoUrl = photoUrl,
    photoThumbnailUrl = photoThumbnailUrl,
    createdAt = OffsetDateTime.parse(createdAt).toInstant().toEpochMilli(),
    // Коды роли сервера совпадают с PetViewerRoles; неизвестная роль не пройдёт фильтр «мои питомцы»
    viewerRole = viewerRole,
)

/** Интересы питомца из [PetProfileResponse]. */
internal fun PetProfileResponse.toInterestEntities() = interests.map { code ->
    PetInterestEntity(petId = id, code = code)
}

/** Владельцы питомца из [PetCardResponse] в том порядке, в котором их отдал сервер. */
internal fun PetCardResponse.toOwnerEntities() = owners.mapIndexed { index, owner ->
    PetOwnerEntity(petId = pet.id, userId = owner.user.id, role = owner.role, position = index)
}

/** Маппинг [UserSnippetResponse] в [UserSnippetEntity]. */
internal fun UserSnippetResponse.toEntity() = UserSnippetEntity(
    id = id,
    firstName = firstName,
    lastName = lastName,
    login = login,
    photoThumbnailUrl = photoThumbnailUrl,
)

// Entity → Domain. Код, неизвестный этой версии приложения, не роняет чтение: для него есть значение по
// умолчанию, а где его нет — отбрасывается элемент списка.

/** Маппинг [PetEntity] в [PetSummary]. */
internal fun PetEntity.toSummary() = PetSummary(
    id = id,
    name = name,
    breed = PetBreed.fromCodeOrNull(breed) ?: PetBreed.MIXED_BREED,
    photoUrl = photoUrl ?: "",
    photoThumbnailUrl = photoThumbnailUrl ?: "",
    status = status.toPetStatus(),
    isOwner = viewerRole == PetViewerRoles.OWNER,
)

/**
 * Маппинг [PetWithDetails] в [PetDetail]; `null`, если код пола неизвестен. Пол приходит с сервера enum-ом
 * из двух значений, так что на практике этого не бывает.
 */
internal fun PetWithDetails.toDetail(): PetDetail? {
    val sex = pet.sex.toEnumOrNull<PetSex>() ?: return null
    return PetDetail(
        id = pet.id,
        name = pet.name,
        birthDate = pet.birthDate,
        breed = PetBreed.fromCodeOrNull(pet.breed) ?: PetBreed.MIXED_BREED,
        sex = sex,
        weightGrams = pet.weightGrams,
        interests = interestCodes.mapNotNull { PetInterest.fromCodeOrNull(it) },
        description = pet.description ?: "",
        photoUrl = pet.photoUrl ?: "",
        photoThumbnailUrl = pet.photoThumbnailUrl ?: "",
        status = pet.status.toPetStatus(),
        // Неизвестная роль не даёт прав: питомец считается чужим
        viewerRole = pet.viewerRole.toEnumOrNull<PetViewerRole>() ?: PetViewerRole.OTHER,
        owners = owners.sortedBy { it.owner.position }.mapNotNull { it.toDomain() },
    )
}

/** Владелец с неизвестной ролью не показывается: непонятно, как его подписать. */
private fun PetOwnerWithUser.toDomain(): PetOwner? {
    val role = owner.role.toEnumOrNull<PetOwnerRole>() ?: return null
    return PetOwner(user = user.toSummary(), role = role)
}

private fun String.toPetStatus() = toEnumOrNull<PetStatus>() ?: PetStatus.UNKNOWN

private fun UserEntity.toSummary() = UserSummary(
    id = id,
    firstName = firstName,
    lastName = lastName,
    login = login ?: "",
    photoThumbnailUrl = photoThumbnailUrl ?: "",
)

/** Коды в базе совпадают с именами констант доменных enum. */
private inline fun <reified T : Enum<T>> String.toEnumOrNull(): T? = enumValues<T>().firstOrNull { it.name == this }
