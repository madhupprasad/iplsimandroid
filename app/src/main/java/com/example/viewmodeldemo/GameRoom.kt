package com.example.viewmodeldemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.viewmodeldemo.databinding.FragmentGameRoomBinding
import com.google.gson.Gson
import org.json.JSONObject


class GameRoom : Fragment() {
    private lateinit var binding : FragmentGameRoomBinding
    private var bidAmount : Int = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val mSocket = SocketHandler.getSocket()
                val rid = requireArguments().getString("room_id")
                val pid = requireArguments().getString("player_id")
                var gson = Gson()
                var data = object {
                    val roomId : String? = rid
                    val playerId : String? = pid
                }
                var jsonString = gson.toJson(data)
                mSocket.emit("end", jsonString)

                findNavController().navigate(R.id.action_gameRoom_to_roomInfo2)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGameRoomBinding.inflate(inflater, container, false)
        val rid = requireArguments().getString("room_id")
        val pid = requireArguments().getString("player_id")

        val mSocket = SocketHandler.getSocket()
        var gson = Gson()

        var data = object {
            val roomId = rid
            val playerId = pid
        }

        mSocket.emit("start_game",gson.toJson(data))

        mSocket.on("balance_update"){
            args -> if(args[0] != null){
                activity?.runOnUiThread {
                    binding.tvBalance.text = args[0].toString()
                }
            }
        }

        mSocket.on("after_bid"){
            args -> if(args[0]!=null){
                val data = args[0] as JSONObject
            activity?.runOnUiThread {
                binding.tvHighestBid.text = "Current Bid ${data.get("highestBid").toString()}"
                binding.tvHighestBidder.text = "By ${ data.get("playerName").toString() }"
            }
        }
        }

        mSocket.on("cricketer_data"){
            args -> if(args[0] != null){
                val cricketer = args[0] as JSONObject
                activity?.runOnUiThread {
                    binding.tvCricketerName.text = cricketer.get("player") as CharSequence?
                    binding.tvBaseBid.text = "Base price : ${cricketer.get("base").toString()}"
                }
            }
        }

        mSocket.on("timer"){
            args -> if(args[0] != null){
                activity?.runOnUiThread {
                    binding.tvTimer.text = args[0].toString()
                }
            }
        }

        mSocket.on("timeout"){
            mSocket.emit("next_player", gson.toJson(data))
        }


        binding.btnBid.setOnClickListener {
            var data = object {
                val roomId = rid
                val playerId = pid
                val amount = bidAmount
            }
            mSocket.emit("after_bid", gson.toJson(data) )
        }

        return binding.root
    }
}