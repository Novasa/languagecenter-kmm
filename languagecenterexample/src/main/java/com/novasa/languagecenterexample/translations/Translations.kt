package com.novasa.languagecenterexample.translations

import com.novasa.languagecenter.domain.model.LanguageCenterCategory

object Translations {

    object Test : LanguageCenterCategory() {

        object Test1 : Translation(fallback = "Fallback text 1")

        object Test2 : Translation(fallback = "Fallback text 2")

        object Test3 : Translation(fallback = "Fallback text 3") {
            override val comment: String = "Comment for test 3"
        }
    }

    object LanguagecenterTest : LanguageCenterCategory() {

        object Basic : Translation(fallback = "Language Center works?")
        object Danish : Translation(fallback = "Danish")
        object English : Translation(fallback = "English")
        object Chinese : Translation(fallback = "Chinese")
        object UnsupportedLanguage : Translation(fallback = "Unsupported language")
    }
}
