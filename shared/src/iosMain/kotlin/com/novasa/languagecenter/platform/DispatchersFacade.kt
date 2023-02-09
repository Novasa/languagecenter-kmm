package com.novasa.languagecenter.platform

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newFixedThreadPoolContext

actual class DispatchersFacade actual constructor() {
    actual val io: CoroutineDispatcher = newFixedThreadPoolContext(nThreads = 200, name = "IO")
    actual val main: CoroutineDispatcher = Dispatchers.Main
}
