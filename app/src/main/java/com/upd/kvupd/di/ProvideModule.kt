package com.upd.kvupd.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.upd.kvupd.data.local.DataBaseInitializer
import com.upd.kvupd.data.local.cache.CacheCrud
import com.upd.kvupd.data.local.cache.CacheQuery
import com.upd.kvupd.data.local.cache.CacheRoom
import com.upd.kvupd.data.local.core.CoreCrud
import com.upd.kvupd.data.local.core.CoreQuery
import com.upd.kvupd.data.local.core.CoreRoom
import com.upd.kvupd.data.remote.FirebaseHelper
import com.upd.kvupd.data.remote.FlexibleAdapterMoshi
import com.upd.kvupd.utils.SharedPreferenceKeys.SHARED_NOMBRE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProvideModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(SHARED_NOMBRE, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideCoreDataBase(initializer: DataBaseInitializer): CoreRoom =
        initializer.buildCore()


    @Singleton
    @Provides
    fun provideCacheDataBase(initializer: DataBaseInitializer): CacheRoom =
        initializer.buildCache()


    @Provides
    fun provideCoreCrudDAO(db: CoreRoom): CoreCrud =
        db.getCrudDao()


    @Provides
    fun provideCacheCrudDAO(db: CacheRoom): CacheCrud =
        db.getCrudDao()


    @Provides
    fun provideCoreQueryDAO(db: CoreRoom): CoreQuery =
        db.getQueryDao()


    @Provides
    fun provideCacheQueryDAO(db: CacheRoom): CacheQuery =
        db.getQueryDao()

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(FlexibleAdapterMoshi())
        .add(KotlinJsonAdapterFactory())
        .build()

    @Singleton
    @Provides
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseHelper(firebaseDatabase: FirebaseDatabase): FirebaseHelper =
        FirebaseHelper(firebaseDatabase)

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}