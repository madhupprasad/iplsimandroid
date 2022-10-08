package com.example.viewmodeldemo

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CreateRoomService {

    @POST("/api/get_info")
    suspend fun createRoom(@Body createRoomData : CreateRoomData): Response<CreateRoomResponse>

}