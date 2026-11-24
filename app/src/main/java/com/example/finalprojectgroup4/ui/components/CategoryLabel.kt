package com.example.finalprojectgroup4.ui.components

import androidx.annotation.StringRes
import com.example.finalprojectgroup4.R
import com.example.finalprojectgroup4.data.model.Category

/**
 * The display name of a category, from strings.xml.
 * Use with stringResource(category.labelRes()).
 *
 * Kind: extension function on the Category enum. Owner: Track A (Sébastien).
 */
@StringRes
fun Category.labelRes(): Int = when (this) {
    Category.RESTAURANT -> R.string.category_restaurant
    Category.EVENT -> R.string.category_event
    Category.OTHER -> R.string.category_other
}
