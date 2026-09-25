package com.xando.design.utils

/**
 * Разделитель «·». Перед точкой неразрывный пробел: при переносе строка не начнётся с точки.
 */
private const val DOT_SEPARATOR = " · "

/**
 * Склеивает части через разделитель «·», например «Дружат · 31». Пустые и пробельные части
 * пропускаются, поэтому необязательные значения можно передавать как есть.
 *
 * @param parts Части строки в порядке вывода.
 */
fun joinWithDot(vararg parts: String): String =
    parts.filter { it.isNotBlank() }.joinToString(DOT_SEPARATOR)

/**
 * Сокращённое имя человека: имя и первая буква фамилии с точкой, например «Анна К.». Если
 * фамилия пустая, возвращает только имя.
 *
 * @param firstName Имя.
 * @param lastName Фамилия.
 */
fun formatShortName(firstName: String, lastName: String): String {
    val name = firstName.trim()
    val surname = lastName.trim()
    if (surname.isEmpty()) return name

    val initial = surname.first().uppercase()
    return "$name $initial."
}
