package com.novasa.languagecenter.domain.model

import com.novasa.languagecenter.extension.toSnakeCase

interface LanguageCenterValue {
    val category: String
    val key: String
    val fallback: String
    val comment: String?
        get() = null
}

/**
 * Used to organize translations into categories.
 *
 * Values for category and key will be extracted from the object names.
 *
 * Usage:
 * ```
 * object InterestingCategory : LanguageCenterCategory() {
 *     // Key will be "interesting_category.text1"
 *     object Text1 : Translation(fallback = "Interesting fallback text 1")
 *     object Text2 : Translation(fallback = "Interesting fallback text 2")
 * }
 *
 * object ExcitingCategory : LanguageCenterCategory() {
 *     object Text : Translation(
 *         fallback = "Exciting fallback text",
 *         comment = "Comment describing exciting text in exiting category"
 *     )
 * }
 * ```
 */
abstract class LanguageCenterCategory {
    open val category = this::class.simpleName ?: throw IllegalArgumentException()

    abstract inner class Translation(
        override val fallback: String,
        override val comment: String? = null

    ) : LanguageCenterValue {
        override val category: String = this@LanguageCenterCategory.category.toSnakeCase()
        override val key: String = this::class.simpleName?.toSnakeCase() ?: throw IllegalArgumentException()
    }
}
