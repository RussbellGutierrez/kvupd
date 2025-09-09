package com.upd.kvupd.di

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkManager
import com.google.firebase.database.FirebaseDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.upd.kvupd.data.local.DataBaseInitializer
import com.upd.kvupd.data.local.Crud
import com.upd.kvupd.data.local.QueryList
import com.upd.kvupd.data.local.TablesRoom
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
object ProviderModule {

    @Provides
    @Singleton
    fun providerSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(SHARED_NOMBRE, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun providerDataBase(initializer: DataBaseInitializer): TablesRoom =
        initializer.build()

    @Provides
    fun providerCrudDAO(db: TablesRoom): Crud = db.getCrudDao()

    @Provides
    fun providerQueryDAO(db: TablesRoom): QueryList = db.getQueryDao()

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
    fun providerFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Singleton
    @Provides
    fun providerFirebaseHelper(firebaseDatabase: FirebaseDatabase): FirebaseHelper =
        FirebaseHelper(firebaseDatabase)

    @Provides
    @Singleton
    fun providerWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

    @Provides
    fun providerNotificationManager(@ApplicationContext context: Context): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}