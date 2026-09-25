package com.xando.design.utils

import android.content.res.Resources
import com.xando.core.design.R
import java.time.LocalDate
import java.time.Period

/**
 * Возраст словами: «2 года и 3 месяца», «2 года», «5 месяцев» или «меньше месяца». Месяцы при
 * нулевом значении не пишутся. Дата рождения позже [today] даёт «меньше месяца».
 *
 * @param resources Ресурсы для строк на языке приложения.
 * @param birthDate Дата рождения.
 * @param today Дата, на которую считается возраст.
 */
fun formatAge(resources: Resources, birthDate: LocalDate, today: LocalDate = LocalDate.now()): String {
    val age = Period.between(birthDate, today)
    val years = age.years
    val months = age.months

    if (age.isNegative || (years == 0 && months == 0)) {
        return resources.getString(R.string.design_age_less_than_month)
    }

    val monthsText = resources.getQuantityString(R.plurals.design_age_months, months, months)
    if (years == 0) return monthsText

    val yearsText = resources.getQuantityString(R.plurals.design_age_years, years, years)
    if (months == 0) return yearsText

    return resources.getString(R.string.design_age_years_and_months, yearsText, monthsText)
}
