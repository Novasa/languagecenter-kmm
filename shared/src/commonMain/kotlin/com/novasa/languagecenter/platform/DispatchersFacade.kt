package com.novasa.languagecenter.platform

import kotlinx.coroutines.CoroutineDispatcher

expect class DispatchersFacade() {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
}
