package pl.artoch.maps_app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import pl.artoch.maps_app.MapsContract
import pl.artoch.maps_app.MapsViewModel

@Module
@InstallIn(ViewModelComponent::class)
interface ViewModelModule {

    @Binds
    @ViewModelScoped
    fun bindMyViewModel(viewModel: MapsViewModel): MapsContract.ViewModel
}
