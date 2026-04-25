package com.xando.design.ui.components.search

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.xando.core.design.R
import com.xando.design.ui.components.text_field.StayaOutlinedTextField
import com.xando.design.ui.theme.extendedColors

/**
 * Объект, содержащий дефолтные значения для компонента [StayaOutlinedTextField]
 */
object StayaSearchPanelDefaults {

    /**
     * Стиль текста по умолчанию
     */
    val textStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography.bodyLarge

    /**
     * Форма углов по умолчанию
     */
    val shape: Shape = CircleShape
}

/**
 * Панель поиска с однострочным текстовым полем и клавишей «Поиск» на клавиатуре.
 */
@Composable
fun StayaSearchPanel(
    searchQuery: String,
    onValueChange: (String) -> Unit,
    placeholder: String = stringResource(R.string.design_search_placeholder),
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.extendedColors.unaccentedTextColor
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.design_ic_search_24px),
                contentDescription = null
            )
        },
        singleLine = true,
        textStyle = StayaSearchPanelDefaults.textStyle,
        shape = StayaSearchPanelDefaults.shape,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.extendedColors.unaccentedBackgroundColor,
            unfocusedContainerColor = MaterialTheme.extendedColors.unaccentedBackgroundColor,
            disabledContainerColor = MaterialTheme.extendedColors.unaccentedBackgroundColor,
            focusedBorderColor = MaterialTheme.extendedColors.unaccentedBackgroundColor,
            unfocusedBorderColor = MaterialTheme.extendedColors.unaccentedBackgroundColor,
        ),
    )
}