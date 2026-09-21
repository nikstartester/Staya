@file:OptIn(InternalSerializationApi::class)

package com.xando.data.pet.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Сетевой ответ на загрузку файла.
 *
 * @property fileId Идентификатор загруженного файла.
 */
@Serializable
internal data class FileUploadResponse(
    val fileId: String,
)
