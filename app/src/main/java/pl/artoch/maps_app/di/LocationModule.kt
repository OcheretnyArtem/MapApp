package pl.artoch.maps_app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.artoch.maps_app.location.LocationManager
import pl.artoch.maps_app.location.LocationManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface LocationModule {

    @Binds
    @Singleton
    fun bindLocationManager(locationManagerImpl: LocationManagerImpl): LocationManager
}
