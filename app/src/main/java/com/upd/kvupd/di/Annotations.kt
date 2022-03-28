package com.upd.kvupd.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocationRequestGps

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocationRequestPosition

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocationSettingsRequestGps

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocationSettingsRequestPosition
