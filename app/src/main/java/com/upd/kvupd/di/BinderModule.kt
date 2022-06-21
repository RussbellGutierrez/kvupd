package com.upd.kvupd.di

import com.upd.kvupd.domain.FunImpl
import com.upd.kvupd.domain.Functions
import com.upd.kvupd.domain.RepoImpl
import com.upd.kvupd.domain.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BinderModule {

    @Singleton
    @Binds
    abstract fun bindRepoImpl(repo: RepoImpl): Repository

    @Singleton
    @Binds
    abstract fun bindFunImpl(fnt: FunImpl): Functions
}