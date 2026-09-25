package com.xando.core.database.pet

import androidx.room3.ColumnInfo

/**
 * Отметки пользователей о питомце - часть [PetEntity].
 *
 * Пишется в `pets` частичным update: у сохранённого питомца обновляются только эти поля.
 *
 * @property id Идентификатор питомца на сервере.
 * @property friendsCount Сколько пользователей отметили, что дружат; `null`, если неизвестно.
 * @property notFriendsCount Сколько пользователей отметили, что не дружат; `null`, если неизвестно.
 * @property myRelation Код отметки текущего пользователя; `null`, если отметки нет.
 */
data class PetRelationsEntity(
    val id: String,
    @ColumnInfo(name = "friends_count") val friendsCount: Int?,
    @ColumnInfo(name = "not_friends_count") val notFriendsCount: Int?,
    @ColumnInfo(name = "my_relation") val myRelation: String?,
)
