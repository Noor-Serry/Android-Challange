package com.hamza.ieeechallenge.ui.auth.country

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamza.ieeechallenge.data.model.CountryCallingCodes
import com.hamza.ieeechallenge.data.repositories.UtilRepository
import com.hamza.ieeechallenge.utils.Resource
import com.google.firebase.firestore.ktx.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryViewModel @Inject constructor(private val utilRepository: UtilRepository) : ViewModel() {

    private val _countryList = MutableLiveData<Resource<List<CountryCallingCodes>>>()
    val countryList: LiveData<Resource<List<CountryCallingCodes>>>
        get() = _countryList

    init {
        getCountryList()
    }

    private fun getCountryList() {
        _countryList.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = utilRepository.getCountryList()
                    .toObjects<CountryCallingCodes>()
                _countryList.postValue(Resource.Success(result))
            } catch (e: Exception) {
                e.printStackTrace()
                _countryList.postValue(Resource.Error(message =  e.message))
            }
        }
    }
}