package com.hamza.ieeechallenge.ui.home.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.hamza.ieeechallenge.R
import com.hamza.ieeechallenge.databinding.FragmentChatTabBinding
import com.hamza.ieeechallenge.utils.Resource


class ChatTabFragment : Fragment() {

    private val conversationViewModel: ConversationViewModel by viewModels()
    private lateinit var conversationAdapter: ChatConversationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentChatTabBinding.inflate(inflater, container, false)

        conversationViewModel.getConversation()

        conversationViewModel.conversationList.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success<*> -> {
                    response.data?.let { conversationList ->
                        conversationAdapter =
                            ChatConversationAdapter(ChatConversationAdapter.ConversationListener {
                                Toast.makeText(context, "toast ${it.lastMessage}", Toast.LENGTH_SHORT).show()
                            })
                        conversationAdapter.submitList(conversationList)

                        conversationViewModel.userList.observe(viewLifecycleOwner, Observer {
                            conversationAdapter.setData(it)
                            binding.rvChatTab.adapter = conversationAdapter

                        })

                    }
                }
            }

        })




        return binding.root
    }


}