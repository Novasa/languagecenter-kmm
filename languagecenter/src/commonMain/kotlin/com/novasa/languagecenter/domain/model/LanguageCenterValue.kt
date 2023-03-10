package com.novasa.languagecenter.domain.model

import com.novasa.languagecenter.extension.toSnakeCase

/**
 * Defines a translatable string.
 *
 * The key of the string will be generated from [category] concatenated with [id] as such:
 *
 * "category.id".
 */
interface LanguageCenterValue {
    val id: String
    val category: String
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
    open val category = this::class.simpleName?.toSnakeCase() ?: throw IllegalArgumentException()

    abstract inner class Translation(
        override val fallback: String,
        override val comment: String? = null

    ) : LanguageCenterValue {
        override val id: String = this::class.simpleName?.toSnakeCase() ?: throw IllegalArgumentException()
        override val category: String = this@LanguageCenterCategory.category
    }
}

/** Default data class implementation of [LanguageCenterValue] */
data class DefaultLanguageCenterValue(
    override val id: String,
    override val category: String,
    override val fallback: String,
    override val comment: String? = null
) : LanguageCenterValue
