package com.upd.kvupd.di

import com.upd.kvupd.domain.IdentityFunctions
import com.upd.kvupd.domain.IdentityImplementation
import com.upd.kvupd.domain.JsObFunctions
import com.upd.kvupd.domain.JsObImplementation
import com.upd.kvupd.domain.OldFunImpl
import com.upd.kvupd.domain.OldFunctions
import com.upd.kvupd.domain.OldRepoImpl
import com.upd.kvupd.domain.OldRepository
import com.upd.kvupd.domain.RoomFunctions
import com.upd.kvupd.domain.RoomImplementation
import com.upd.kvupd.domain.ServerFunctions
import com.upd.kvupd.domain.ServerImplementation
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
    abstract fun bindIdentityImplementation(identity: IdentityImplementation): IdentityFunctions

    @Singleton
    @Binds
    abstract fun bindServerImplementation(server: ServerImplementation): ServerFunctions

    @Singleton
    @Binds
    abstract fun bindJsObImplementation(json: JsObImplementation): JsObFunctions

    @Singleton
    @Binds
    abstract fun bindRoomImplementation(room: RoomImplementation): RoomFunctions

    // ELIMINAR LUEGO
    @Singleton
    @Binds
    abstract fun bindRepoImpl(repo: OldRepoImpl): OldRepository

    @Singleton
    @Binds
    abstract fun bindFunImpl(fnt: OldFunImpl): OldFunctions
}