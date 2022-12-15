package com.upd.kvupd.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.WorkManager
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.squareup.moshi.Moshi
import com.upd.kvupd.data.local.AppDB
import com.upd.kvupd.data.model.JSONObjectAdapter
import com.upd.kvupd.data.remote.ApiClient
import com.upd.kvupd.utils.Constant.BASE_URL
import com.upd.kvupd.utils.Constant.DB_NAME
import com.upd.kvupd.utils.Constant.GPS_FAST_INTERVAL
import com.upd.kvupd.utils.Constant.GPS_NORMAL_INTERVAL
import com.upd.kvupd.utils.Constant.POSITION_F_INTERVAL
import com.upd.kvupd.utils.Constant.POSITION_N_INTERVAL
import com.upd.kvupd.utils.HostSelectionInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProviderModule {

    @Singleton
    @Provides
    fun providerDB(@ApplicationContext ctx: Context) =
        Room.databaseBuilder(ctx, AppDB::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun providerDao(db: AppDB) = db.getDao()

    @Singleton
    @Provides
    fun providerQueryDao(db: AppDB) = db.getQDao()

    @Singleton
    @Provides
    fun providerMoshi() =
        MoshiConverterFactory.create(Moshi.Builder().add(JSONObjectAdapter()).build())

    @Singleton
    @Provides
    fun providerHostSelectionInterceptor() =
        HostSelectionInterceptor()

    @Singleton
    @Provides
    fun providerHttpClient(
        hostSelectionInterceptor: HostSelectionInterceptor
    ): OkHttpClient {
        return OkHttpClient
            .Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(hostSelectionInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun providerRetrofit(
        okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(moshiConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun providerApi(retrofit: Retrofit) = retrofit.create(ApiClient::class.java)

    @Singleton
    @Provides
    fun providerWorkManager(@ApplicationContext ctx: Context) = WorkManager.getInstance(ctx)

    @LocationRequestGps
    @Singleton
    @Provides
    fun providerLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = GPS_NORMAL_INTERVAL
            fastestInterval = GPS_FAST_INTERVAL
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    @LocationSettingsRequestGps
    @Singleton
    @Provides
    fun providerLocationSettingsRequest(@LocationRequestGps locationRequest: LocationRequest) =
        LocationSettingsRequest.Builder().apply {
            addLocationRequest(locationRequest)
        }.build()

    @LocationRequestPosition
    @Singleton
    @Provides
    fun providerLocationRequestP(): LocationRequest {
        return LocationRequest.create().apply {
            interval = POSITION_N_INTERVAL
            fastestInterval = POSITION_F_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    @LocationSettingsRequestPosition
    @Singleton
    @Provides
    fun providerLocationSettingsRequestP(@LocationRequestPosition locationRequest: LocationRequest) =
        LocationSettingsRequest.Builder().apply {
            addLocationRequest(locationRequest)
        }.build()
}