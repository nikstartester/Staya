package com.xando.data.image

import androidx.work.Data
import androidx.work.ListenableWorker

/**
 * Адресат отправки: какой работой и в какой ресурс уходит фотография.
 *
 * Объявляет его тот модуль, которому ресурс принадлежит, а не фиче экраны.
 * Так две фичи, отправляющие одну и ту же фотографию, гарантированно берут одно и то же [workName] и не
 * могут разъехаться.
 *
 * @property workerClass Воркер, выполняющий отправку.
 * @property workName Имя уникальной работы. Отправки в один ресурс вытесняют друг друга, в разные -
 * не мешают: одна фотография ресурса всегда отменяет предыдущую, ещё не отправленную.
 * @property extras Дополнительные данные воркеру, если адресату мало одного uri фотографии.
 */
data class PhotoUploadTarget(
    val workerClass: Class<out ListenableWorker>,
    val workName: String,
    val extras: Data = Data.EMPTY,
)
