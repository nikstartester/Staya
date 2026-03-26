package com.xando.auth.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.LocaleList
import com.xando.design.ui.components.text_field.StayaOutlinedTextField
import com.xando.core.design.R as RDesign

@Composable
internal fun EmailTextField(
    value: String,
    label: String,
    onValueChanged: (String) -> Unit,
    readOnly: Boolean,
    errorText: String? = null,
) {
    StayaOutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        label = label,
        modifier = Modifier.fillMaxWidth(),
        readOnly = readOnly,
        singleLine = true,
        leadingIcon = {
            Icon(
                painter = painterResource(RDesign.drawable.design_ic_mail_24dp),
                contentDescription = null
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, hintLocales = LocaleList("en")),
        errorText = errorText
    )
}
