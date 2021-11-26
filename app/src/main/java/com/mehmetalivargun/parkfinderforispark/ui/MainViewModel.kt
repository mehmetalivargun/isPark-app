package com.mehmetalivargun.parkfinderforispark.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehmetalivargun.parkfinderforispark.data.remote.Parks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository)  : ViewModel(){
    private val _parks = MutableLiveData<Parks>()
    val parks : LiveData<Parks> = _parks
    init {
        getProducts()
    }
    private fun getProducts() = viewModelScope.launch {
        repository.fetchParks().collect {
            when(it){
                is MainRepository.ParkListResult.Success->onSucces(it.parks)
                MainRepository.ParkListResult.Failure->onFailure()
                MainRepository.ParkListResult.Loading-> onLoading()
                MainRepository.ParkListResult.UnexpectedError->onUnexpected()
            }
        }
    }

    private fun onUnexpected() {

    }

    private fun onLoading() {

    }

    private fun onFailure() {

    }

    private fun onSucces(parks: Parks) {
        _parks.postValue(parks)

    }

}

