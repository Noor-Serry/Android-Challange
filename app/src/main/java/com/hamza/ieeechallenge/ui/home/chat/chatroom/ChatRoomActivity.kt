package com.hamza.ieeechallenge.ui.home.chat.chatroom

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hamza.ieeechallenge.R
import com.hamza.ieeechallenge.data.model.User
import com.hamza.ieeechallenge.databinding.ActivityChatRoomBinding
import com.hamza.ieeechallenge.ui.profile.UserViewModel

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding
    private val chatViewModel: ChatViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_room)

        val user = intent.getParcelableExtra<User>("user")

        binding.viewModel = chatViewModel
        binding.lifecycleOwner = this


        chatViewModel.setUserData(user)

        userViewModel.getReceiverUidByPhoneNumber(user?.phone!!)


        binding.etMessageChat.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                if (charSequence!!.isEmpty()) {
                    binding.fabSend.setOnClickListener {
                        chatViewModel.sendConversation(userViewModel.receiverId.value!!)
                    }
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }
}