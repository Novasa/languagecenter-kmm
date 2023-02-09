package com.novasa.languagecenter.domain.provider

import com.novasa.languagecenter.domain.model.LanguageCenterConfig
import com.novasa.languagecenter.domain.model.LanguageCenterLanguage
import com.novasa.languagecenter.domain.model.LanguageCenterStatus
import com.novasa.languagecenter.domain.model.LanguageCenterValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface LanguageCenterProvider {

    val status: StateFlow<LanguageCenterStatus>

    val activeLanguage: StateFlow<LanguageCenterLanguage>

    fun initialize(config: LanguageCenterConfig)

    fun setLanguage(language: String)

    fun getTranslation(value: LanguageCenterValue) : Flow<String>
}
