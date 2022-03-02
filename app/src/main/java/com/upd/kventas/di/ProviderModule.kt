package com.upd.kventas.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.squareup.moshi.Moshi
import com.upd.kventas.data.local.AppDB
import com.upd.kventas.data.model.JSONObjectAdapter
import com.upd.kventas.data.remote.ApiClient
import com.upd.kventas.utils.Constant.BASE_URL
import com.upd.kventas.utils.Constant.DB_NAME
import com.upd.kventas.utils.Constant.GPS_FAST_INTERVAL
import com.upd.kventas.utils.Constant.GPS_NORMAL_INTERVAL
import com.upd.kventas.utils.Constant.POSITION_F_INTERVAL
import com.upd.kventas.utils.Constant.POSITION_N_INTERVAL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ProviderModule {

    @Singleton
    @Provides
    fun providerDB(@ApplicationContext ctx: Context) =
        Room.databaseBuilder(ctx, AppDB::class.java, DB_NAME).build()

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
    fun providerHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun providerRetrofit(
        okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
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
    fun providerLocationSettingsRequest(@LocationRequestGps locationRequest: LocationRequest) = LocationSettingsRequest.Builder().apply {
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
    fun providerLocationSettingsRequestP(@LocationRequestPosition locationRequest: LocationRequest) = LocationSettingsRequest.Builder().apply {
        addLocationRequest(locationRequest)
    }.build()
}