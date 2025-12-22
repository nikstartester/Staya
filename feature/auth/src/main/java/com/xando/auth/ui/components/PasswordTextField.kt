package com.xando.auth.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.intl.LocaleList
import com.xando.design.ui.components.text_field.StayaOutlinedTextField
import com.xando.feature.auth.R
import com.xando.core.design.R as RDesign

@Composable
internal fun PasswordTextField(value: String, onValueChange: (String) -> Unit, enabled: Boolean) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    StayaOutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = stringResource(R.string.auth_password_title),
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        leadingIcon = {
            Icon(
                painter = painterResource(RDesign.drawable.design_ic_lock_24dp),
                contentDescription = null,
            )
        },
        trailingIcon =
            @Composable {
                val visibilityIconRes =
                    if (isPasswordVisible) RDesign.drawable.design_ic_visibility_off_24dp
                    else RDesign.drawable.design_ic_visibility_24dp
                AnimatedVisibility(
                    value.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Icon(
                        modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible },
                        painter = painterResource(visibilityIconRes),
                        contentDescription = null
                    )
                }
            },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, hintLocales = LocaleList("en"))
    )
}