package com.novasa.languagecenter.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.novasa.languagecenter.domain.model.LanguageCenterValue

@Composable
fun translation(translation: LanguageCenterValue) = LocalLanguageCenter.current.getTranslation(translation)
    .collectAsState(initial = translation.fallback)
    .value

@Composable
operator fun LanguageCenterValue.invoke() = translation(this)
