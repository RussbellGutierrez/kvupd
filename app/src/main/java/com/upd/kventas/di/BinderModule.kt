package com.upd.kventas.di

import com.upd.kventas.domain.FunImpl
import com.upd.kventas.domain.Functions
import com.upd.kventas.domain.RepoImpl
import com.upd.kventas.domain.Repository
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