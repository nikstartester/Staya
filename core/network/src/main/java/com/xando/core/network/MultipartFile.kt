package com.xando.core.network

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

/**
 * Тело multipart-запроса с одним файлом и необязательными текстовыми полями формы.
 *
 * @param bytes Содержимое файла.
 * @param contentType MIME-тип файла, например `image/jpeg`.
 * @param fileName Имя файла в `Content-Disposition`; сервер видит его как исходное имя файла.
 * @param partName Имя части с файлом.
 * @param fields Текстовые поля формы, которые уходят вместе с файлом.
 */
fun multipartFile(
    bytes: ByteArray,
    contentType: String,
    fileName: String,
    partName: String = "file",
    fields: Map<String, String> = emptyMap(),
): MultiPartFormDataContent = MultiPartFormDataContent(
    formData {
        fields.forEach { (key, value) -> append(key, value) }
        append(
            key = partName,
            value = bytes,
            headers = Headers.build {
                append(HttpHeaders.ContentType, contentType)
                append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
            }
        )
    }
)
