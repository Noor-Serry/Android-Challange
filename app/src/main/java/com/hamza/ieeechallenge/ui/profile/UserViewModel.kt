package com.hamza.ieeechallenge.ui.profile

import android.net.Uri
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.hamza.ieeechallenge.data.model.User
import com.hamza.ieeechallenge.data.repositories.UserRepository
import com.hamza.ieeechallenge.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val currentUser = firebaseAuth.currentUser!!

    val username = MutableLiveData<String>()

    val dataImageUri = MutableLiveData<Uri>()

    private val _response = MutableLiveData<Resource<Any>>()
    val response: LiveData<Resource<Any>>
        get() = _response

    private val _contactResponse = MutableLiveData<Resource<List<User>>>()
    val contactResponse: LiveData<Resource<List<User>>>
        get() = _contactResponse

    private val _user = MutableLiveData<Resource<User>>()
    val user: LiveData<Resource<User>>
        get() = _user

    private val _receiverUid = MutableLiveData<String>()
    val receiverId: LiveData<String>
        get() = _receiverUid

    private val _chatWithId = MutableLiveData<String>()
    val chatWithId: LiveData<String>
        get() = _chatWithId

    val tes: String? = null

    val testList = MutableLiveData<MutableList<User?>>()
    val list: MutableList<User?> = ArrayList()

    init {
        checkPhoneNumber()
    }

    private fun checkPhoneNumber() {
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                userRepository.checkIfPhoneNumberAlreadyRegistered(currentUser.phoneNumber)
            if (!response.isEmpty) {
                loadUserIfExist()
            }
        }
    }

    private fun loadUserIfExist() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = userRepository.loadExistedUser(currentUser.uid).toObjects<User>()
            for (user in response) {
                withContext(Dispatchers.Main) {
                    username.value = user.username
                    if (user.photoUrl != null) {
                        dataImageUri.value = Uri.parse(user.photoUrl)
                    }
                }
            }

        }
    }

    fun createUser() {
        _response.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            delay(2000L)
            val taskUri: Uri? = if (dataImageUri.value != null) {
                dataImageUri.value
            } else {
                Uri.parse("android.resource://com.hamza.ieeechallenge./drawable/ic_baseline_person_24")
            }
            val uploadTask = uploadAvatar(taskUri)

            val user = User(
                username.value,
                currentUser.phoneNumber,
                uploadTask,
                true,
                0L,
                "Hey there! Welcome to WhatsApp Clone"
            )
            try {
                val result = userRepository.createUser(user, currentUser.uid)
                _response.postValue(Resource.Success(result))
            } catch (e: Exception) {
                _response.postValue(Resource.Error(message = e.message))
            }


        }
    }

    private suspend fun uploadAvatar(uri: Uri?): String? {
        val imageUrl: String?
        imageUrl = try {
            val uploadTask = userRepository.uploadImage(
                currentUser.uid,
                uri!!
            ).metadata?.reference?.downloadUrl?.await()
            uploadTask.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            dataImageUri.value.toString()
        }
        return imageUrl
    }

    fun getContact() {
        Log.i("MYTAG", "getContadt")
        _contactResponse.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val cek: MutableList<User> = mutableListOf()
                val result = userRepository.getUserList().toObjects<User>()
                for (user in result) {
//                    if (user.phone != currentUser.phoneNumber) {
                    cek.add(user)
//                    }
                }
                _contactResponse.postValue(Resource.Success(cek))

            } catch (e: Exception) {
                _contactResponse.postValue(Resource.Error(message = e.message))
            }
        }
    }

    fun getReceiverUidByPhoneNumber(phoneNumber: String) {
        viewModelScope.launch {
            try {
                val result = userRepository.getReceiverUidByPhoneNumber(phoneNumber)
                for (user in result) {
                    _receiverUid.postValue(user.id)
                }
            } catch (e: Exception) {
                Log.i("MYTAG", "Gagal cuy ${e.message}")
            }
        }
    }

    fun getUserByChatWithId(chatWithId: String) {
        userRepository.getUserByChatWithId(chatWithId)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let {
                    Log.i("MYTAG", "Ada yg error ${firebaseFirestoreException.message}")
                    _user.value = Resource.Error(message = firebaseFirestoreException.message)
                    return@addSnapshotListener
                }


                val cek = documentSnapshot?.toObject<User>()
                //Log.i("MYTAG", "ceks ${cek?.username}")
                _user.value = Resource.Success(cek)
                list.add(cek)

                Log.i("MYTAG", "listnya ${list.size}")


            }
    }


    fun setChatWithId(chatWithId: String) {
        _chatWithId.value = chatWithId
    }
}