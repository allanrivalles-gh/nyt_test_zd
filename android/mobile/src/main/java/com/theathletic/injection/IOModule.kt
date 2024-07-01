package com.theathletic.injection

import android.content.Context
import com.theathletic.io.DirectoryProvider
import org.koin.dsl.module

val ioModule = module {
    single { DirectoryProvider(get<Context>()) }
}