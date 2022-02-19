package com.upd.kv.di

import com.upd.kv.domain.FunImpl
import com.upd.kv.domain.Functions
import com.upd.kv.domain.RepoImpl
import com.upd.kv.domain.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class BinderModule {

    @Singleton
    @Binds
    abstract fun bindRepoImpl(repo: RepoImpl): Repository

    @Singleton
    @Binds
    abstract fun bindFunImpl(fnt: FunImpl): Functions
}