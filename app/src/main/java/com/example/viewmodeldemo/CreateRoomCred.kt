package com.example.viewmodeldemo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.viewmodeldemo.databinding.FragmentCreateRoomCredBinding
import com.google.gson.Gson
import retrofit2.Response

class CreateRoomCred : Fragment() {

    private lateinit var binding : FragmentCreateRoomCredBinding
    private var roomid : String = "init"
    private var mutex = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateRoomCredBinding.inflate(inflater, container, false)

//        val retService = RetrofitInstance.getRetrofitInstance().create(CreateRoomService::class.java)
//
//        val createRoomResponse : LiveData<Response<String>> = liveData {
//            Log.i("okok", title)
//            val res = retService.createRoom(CreateRoomData("Madhu", "2"))
//            emit(res)
//        }
//
//        createRoomResponse.observe(viewLifecycleOwner, Observer {
//            val title = it.body() as String?
//            if (title != null) {
//                Log.i("okok", title)
//            }
//        })

        SocketHandler.setSocket()
        val mSocket = SocketHandler.getSocket()
        mSocket.connect()

        mSocket.on("room_id"){
            args -> if(args[0]!=null && args[1] != null){
                activity?.runOnUiThread {
                    roomid = args[0] as String
                    val playerId  = args[1] as String
                    val bundle = bundleOf("room_id" to roomid, "player_id" to playerId)
                    if(mutex){
                        mutex  = false
                        findNavController().navigate(R.id.action_createRoomCred_to_waitScreen, bundle)
                    }
                }
            }
        }

        binding.btnCreateRoom.setOnClickListener {
            var gson = Gson()

            val playerName = binding.etAdminName.text.toString().trim()
            val numberOfPlayers = binding.etNumber.text.toString()

            val nameOk = nameValidator(playerName, context)
            val numOk = numValidator(numberOfPlayers, context)

            if(numOk && nameOk){
                var data = object {
                    val playerName = playerName
                    val numberOfPlayers = numberOfPlayers
                }
                var jsonString = gson.toJson(data)
                mSocket.emit("create", jsonString)
            }
        }

        return binding.root
    }
}