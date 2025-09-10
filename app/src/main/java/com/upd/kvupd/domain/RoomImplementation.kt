package com.upd.kvupd.domain

import com.upd.kvupd.data.local.RoomCrudSource
import com.upd.kvupd.data.local.RoomQuerySource
import javax.inject.Inject

class RoomImplementation @Inject constructor(
    private val crudSource: RoomCrudSource,
    private val querySource: RoomQuerySource
) : RoomFunctions {
}