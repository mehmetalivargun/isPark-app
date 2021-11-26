package com.mehmetalivargun.parkfinderforispark.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehmetalivargun.parkfinderforispark.data.remote.ParkDetail
import com.mehmetalivargun.parkfinderforispark.data.remote.Parks
import com.mehmetalivargun.parkfinderforispark.ui.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: MainRepository) :ViewModel(){
    private val _parks = MutableLiveData<Parks>()
    val parks : LiveData<Parks> = _parks
    private val _parkDetail = MutableLiveData<ParkDetail>()
    val parkDetail : LiveData<ParkDetail> = _parkDetail
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

    fun getDetail(id:Int) = viewModelScope.launch {
        repository.fetchPark(id).collect {
            when(it){
                is MainRepository.ParkResult.Success->onSuccesDetail(it.parks)
                MainRepository.ParkResult.Failure->onFailure()
                MainRepository.ParkResult.Loading-> onLoading()
                MainRepository.ParkResult.UnexpectedError->onUnexpected()
            }
        }
    }

    private fun onSuccesDetail(parks: ParkDetail) {
        _parkDetail.postValue(parks)

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