package com.example.viewmodeldemo

data class CreateRoomData(
    val name : String,
    val maxPlayerCount : String
)

data class CreateRoomResponse(
    val roomid : String
)
