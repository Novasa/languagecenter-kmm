package com.novasa.languagecenterexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.novasa.languagecenter.ui.composable.LocalLanguageCenter
import com.novasa.languagecenter.ui.composable.invoke
import com.novasa.languagecenter.ui.composable.translation
import com.novasa.languagecenterexample.translations.Translations
import com.novasa.languagecenterexample.ui.theme.LanguageCenterTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val lc = LocalLanguageCenter.current

            LanguageCenterTheme {

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(text = Translations.LanguagecenterTest.Basic())

                        Text(text = translation(translation = Translations.Test.Test1))

                        Text(text = translation(translation = Translations.Test.Test2))

                        Text(text = translation(translation = Translations.Test.Test3))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

                            Button(onClick = { lc.setLanguage("da") }) {
                                Text(text = Translations.LanguagecenterTest.Danish())
                            }
                            Button(onClick = { lc.setLanguage("en") }) {
                                Text(text = Translations.LanguagecenterTest.English())
                            }
                            Button(onClick = { lc.setLanguage("zh") }) {
                                Text(text = Translations.LanguagecenterTest.Chinese())
                            }
                        }
                        Button(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = { lc.setLanguage("xx") }
                        ) {
                            Text(text = Translations.LanguagecenterTest.UnsupportedLanguage())
                        }

                        Text(
                            text = "Active language",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(text = lc.activeLanguage.collectAsState().value.toString())

                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(text = lc.status.collectAsState().value.toString())
                    }
                }
            }
        }
    }
}
