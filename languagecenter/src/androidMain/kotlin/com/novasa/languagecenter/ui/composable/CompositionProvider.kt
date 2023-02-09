package com.novasa.languagecenter.ui.composable

import androidx.compose.runtime.compositionLocalOf
import com.novasa.languagecenter.LanguageCenter

val LocalLanguageCenter = compositionLocalOf { LanguageCenter.provider }
