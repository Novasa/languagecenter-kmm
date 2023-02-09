package com.novasa.languagecenter.translations

import com.novasa.languagecenter.domain.model.LanguageCenterValue

sealed interface Translations : LanguageCenterValue {

    sealed class Test(override val key: String, override val fallback: String) : Translations {
        override val category: String = "test"

        object Test1 : Test(key = "test", fallback = "Fallback text 1")

        object Test2 : Test(key = "test2", fallback = "Fallback text 2")

        object Test3 : Test(key = "test3", fallback = "Fallback text 3") {
            override val comment: String = "Comment for test 3"
        }
    }

    sealed class LCTest(override val key: String, override val fallback: String) : Translations {
        override val category: String = "languagecenter_test"

        object Danish : LCTest(key = "danish", fallback = "Danish")
        object English : LCTest(key = "english", fallback = "English")
        object Chinese : LCTest(key = "chinese", fallback = "Chinese")
        object UnsupportedLanguage : LCTest(key = "unsupported_language", fallback = "Unsupported language")
    }
}
