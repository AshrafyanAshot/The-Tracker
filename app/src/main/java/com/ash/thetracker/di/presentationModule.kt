package com.ash.thetracker.di

import com.ash.thetracker.presentation.viewModels.MapsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { MapsViewModel(get()) }
}